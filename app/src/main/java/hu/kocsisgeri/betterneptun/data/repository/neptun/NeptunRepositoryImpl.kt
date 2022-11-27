package hu.kocsisgeri.betterneptun.data.repository.neptun

import android.graphics.Color
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.dao.Message
import hu.kocsisgeri.betterneptun.data.repository.course.HomeState
import hu.kocsisgeri.betterneptun.domain.api.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.domain.api.dto.MessageDto
import hu.kocsisgeri.betterneptun.domain.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.domain.api.network.check
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.ui.model.*
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import hu.kocsisgeri.betterneptun.utils.PREF_STAY_LOGGED_ID
import hu.kocsisgeri.betterneptun.utils.data_manager.DataManager
import hu.kocsisgeri.betterneptun.utils.getSubjectState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil
import kotlin.math.roundToInt

class NeptunRepositoryImpl(
    override val coroutineContext: CoroutineContext = Dispatchers.IO,
    private val networkDataSource: NetworkDataSource,
    private val dataManager: DataManager,
) : NeptunRepository, CoroutineScope {

    override val events = MutableStateFlow<List<CalendarEntity.Event>>(listOf())
    override val messages = MutableStateFlow<ApiResult<List<MessageDto>>>(ApiResult.Progress(0))
    override val currentUser = MutableStateFlow(NeptunUser())
    override val studentData = MutableStateFlow<StudentData?>(null)
    override val markBookData = MutableStateFlow<ApiResult<List<MarkBookDataModel>>>(ApiResult.Progress(0))
    override val averages = MutableStateFlow<ApiResult<List<SemesterModel>>>(ApiResult.Progress(0))
    override var currentMessagePage = 0

    override fun fetchMessages() {
        val save = dataManager.getDefault(PREF_STAY_LOGGED_ID, false)

        launch {
            dataManager.messages.getData().let { cache ->
                val list = mutableListOf<MessageDto>()
                val maxId = cache.maxByOrNull { it.id }?.id ?: 0
                var total: Int
                var counter = 1f
                currentUser.first().let { user ->
                    networkDataSource.getMessages(user.copy(CurrentPage = 0)).let { result ->
                        when (result) {
                            is NetworkResponse.Failure<*> -> 0
                            is NetworkResponse.Success -> result.data.TotalRowCount
                        }
                    }?.let {
                        total = it
                        val lastPage = ceil((it / 10f).toDouble()).roundToInt()
                        if (cache.isEmpty()) {
                            var idx = lastPage
                            currentUser.first().let { user ->
                                while (idx > 0) {
                                    networkDataSource.getMessages(user.copy(CurrentPage = idx))
                                        .let { response ->
                                            when (response) {
                                                is NetworkResponse.Failure<*> -> {}
                                                is NetworkResponse.Success -> response.data.MessagesList?.let { rList ->
                                                    response.data.NewMessagesNumber?.let { unread ->
                                                        HomeState.unreadMessages.tryEmit(unread)
                                                    }
                                                    rList.filter { message -> message.Id > maxId }
                                                        .let { filtered ->
                                                            list.addAll(filtered)
                                                            filtered.saveMessages(save)
                                                        }
                                                }
                                            }
                                        }
                                    val progress = (counter / lastPage) * 100
                                    messages.emit(ApiResult.Progress(progress.roundToInt()))
                                    counter++
                                    idx--
                                }
                                messages.emit(ApiResult.Success(list.sortedByDescending { message -> message.Id }))
                            }
                        } else {
                            currentUser.first().let { user ->
                                var idx = 1f
                                while (idx < lastPage) {
                                    networkDataSource.getMessages(user.copy(CurrentPage = idx.roundToInt()))
                                        .let { response ->
                                            when (response) {
                                                is NetworkResponse.Failure<*> -> {}
                                                is NetworkResponse.Success -> response.data.MessagesList?.let { rList ->
                                                    response.data.NewMessagesNumber?.let { unread ->
                                                        HomeState.unreadMessages.tryEmit(unread)
                                                    }
                                                    rList.filter { message -> message.Id > maxId }
                                                        .let { filtered ->
                                                            if (filtered.isEmpty() && cache.size == total) {
                                                                messages.emit(
                                                                    ApiResult.Success(
                                                                        cache.map { mes ->
                                                                            MessageDto(
                                                                                Id = mes.id,
                                                                                Detail = mes.detail,
                                                                                Name = mes.senderName,
                                                                                Subject = mes.subject,
                                                                                SendDate = mes.date,
                                                                                IsNew = mes.isNew
                                                                            )
                                                                        }.sortedByDescending { message -> message.Id })
                                                                )
                                                                return@launch
                                                            } else {
                                                                list.addAll(rList)
                                                                rList.saveMessages(save)
                                                            }
                                                        }
                                                }
                                            }
                                        }
                                    val progress = (idx / lastPage) * 100
                                    messages.emit(ApiResult.Progress(progress.roundToInt()))
                                    idx++
                                }
                                messages.emit(ApiResult.Success(list.sortedByDescending { message -> message.Id }))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun fetchCalendarData() {
        launch {
            val response = networkDataSource.getCourses()
            val colorMap = mutableMapOf<String?, Int>()
            when (response) {
                is ApiResult.Error -> {/* do something about errors */ }
                is ApiResult.Progress -> {/* don't need to do anything here */ }
                is ApiResult.Success -> {
                    response.data.events.filter { event ->
                        event.allday != 1
                    }.map {
                        CalendarEntity.Event(
                            it.id?.toLong() ?: 1111111,
                            title = it.title?.split("]")?.get(1)?.split("(")?.get(0) ?: "ERROR",
                            startTime = it.startdate?.split("(")?.get(1)?.split(")")?.get(0)
                                ?.toLong()
                                ?.let { longTime ->
                                    LocalDateTime.ofEpochSecond(
                                        longTime / 1000,
                                        0,
                                        ZoneOffset.UTC
                                    )
                                }
                                ?: LocalDateTime.now(),
                            endTime = it.enddate?.split("(")?.get(1)?.split(")")?.get(0)?.toLong()
                                ?.let { longTime ->
                                    LocalDateTime.ofEpochSecond(
                                        longTime / 1000,
                                        0,
                                        ZoneOffset.UTC
                                    )
                                }
                                ?: LocalDateTime.now(),
                            location = it.location.toString(),
                            color = getRandomColor(
                                it.title?.split("]")?.get(1)?.split("(")?.get(0) ?: "ERROR",
                                colorMap
                            ),
                            isAllDay = it.allday != 0,
                            isCanceled = false,
                            subjectCode = it.title?.split("(")?.get(1)?.split(")")?.get(0)
                                ?: "ERROR",
                            courseCode = it.title?.split(" - ")?.get(1)?.split(" ")?.get(0)
                                ?: "ERROR",
                            teacher = it.title?.split("(")?.get(2)?.split(")")?.get(0) ?: "ERROR"
                        )
                    }.let { event ->
                        dataManager.colors.insertAll(event.map {
                            hu.kocsisgeri.betterneptun.data.dao.Color(
                                title = it.title.toString(),
                                colorInt = it.color
                            )
                        })
                        HomeState.courses.tryEmit(event)
                        events.tryEmit(event)
                    }
                }
            }
        }
    }

    override fun fetchMarkBookData() {
        launch {
            currentUser.first().let { user ->
                networkDataSource.getAddedCourses(user).check { subject ->
                    networkDataSource.getMarkBookData(user).check { mark ->
                        val data = subject.AddedSubjectsList.map {
                            MarkBookDataModel(
                                subjectId = it.SubjectID,
                                subjectCode = it.SubjectCode,
                                subjectCredit = it.SubjectCredit,
                                subjectName = it.SubjectName,
                                subjectRequirement = it.SubjectRequirement,
                                subjectType = it.SubjectType,
                                termId = it.TermId,
                                completed = false,
                                signer = "",
                                values = "",
                                state = SubjectState.DEFAULT
                            )
                        }.map { markData ->
                            markData.copy(
                                completed = mark.MarkBookList.firstOrNull {
                                    it.SubjectName == markData.subjectName
                                }?.Completed?:false,
                                signer = mark.MarkBookList.firstOrNull {
                                    it.SubjectName == markData.subjectName
                                }?.Signer?: "",
                                values = mark.MarkBookList.firstOrNull {
                                    it.SubjectName == markData.subjectName
                                }?.Values?: ""
                            )
                        }.map { markData ->
                            markData.copy(
                                state = getSubjectState(markData.completed, markData.signer)
                            )
                        }
                        markBookData.tryEmit(ApiResult.Success(data))
                    }
                }
            }
        }
    }

    override fun fetchAverages() {
        launch {
            networkDataSource.getAverages().let {
                if (it.isNotEmpty()) averages.tryEmit(ApiResult.Success(it))
                else averages.tryEmit(ApiResult.Error("Network error"))
            }
        }
    }

    override suspend fun login(user: NeptunUser): ApiResult<StudentData> = withContext(Dispatchers.IO) {
        networkDataSource.initiateLogin(user.loginRequestData()).let { response ->
            when (response) {
                is NetworkResponse.Failure<*> -> {
                    ApiResult.Error(response.error.getErrorMessage())
                }
                is NetworkResponse.Success -> {
                    if (!response.data.isSuccess()) ApiResult.Error(response.data.getError())
                    else {
                        networkDataSource.getData().let { result ->
                            when (result) {
                                is ApiResult.Error -> ApiResult.Error(result.error)
                                is ApiResult.Progress -> ApiResult.Progress(1)
                                is ApiResult.Success -> ApiResult.Success(result.data)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getRandomColor(title: String?, colorMap: MutableMap<String?, Int>): Int {
        val colors = dataManager.colors.getData()
        val current = colors.firstOrNull { it.title == title }

        val random = IntRange(0, 255)

        val baseColor = Color.BLACK;

        val baseRed = Color.red(baseColor);
        val baseGreen = Color.green(baseColor);
        val baseBlue = Color.blue(baseColor);

        val red = (baseRed + random.random()) / 2;
        val green = (baseGreen + random.random()) / 2;
        val blue = (baseBlue + random.random()) / 2;

        return if (colors.isEmpty()) {
            return if (colorMap.containsKey(title)) {
                colorMap[title]!!
            } else {
                colorMap[title] = Color.rgb(red, green, blue)
                colorMap[title]!!
            }
        } else {
            if (current != null) {
                colorMap[title] = current.colorInt
                colorMap[title]!!
            } else {
                colorMap[title] = Color.rgb(red, green, blue)
                colorMap[title]!!
            }
        }
    }

    override fun readMessage(messageId: Int) {
        launch {
            messages.getSuccess().let { messageList ->
                val newMessage =
                    messageList.firstOrNull { message -> message.Id == messageId }
                if (newMessage?.IsNew == true) {
                    currentUser.first().let { user ->
                        networkDataSource.markMessageAsRead(
                            MessageReader(
                                user.UserLogin,
                                user.Password,
                                user.CurrentPage,
                                messageId
                            )
                        ).check {
                            dataManager.messages.insertOne(
                                Message(
                                    id = newMessage.Id,
                                    detail = newMessage.Detail,
                                    senderName = newMessage.Name,
                                    subject = newMessage.Subject,
                                    date = newMessage.SendDate,
                                    isNew = false
                                )
                            )
                            fetchMessages()
                        }
                    }
                }
            }
        }
    }

    override fun resetMessagePage() {
        currentMessagePage = 0
    }

    override fun randomiseCalendarColors() {
        launch {
            getRandomizedColoredEvents()?.let {
                events.tryEmit(it)
                HomeState.courses.tryEmit(it)
            }
        }
    }

    override fun setEventColor(event: CalendarEntity.Event?, color: Int) {
        launch {
            setEventColorAsync(event, color)?.let {
                events.tryEmit(it)
                HomeState.courses.tryEmit(it)
            }
        }
    }

    private suspend fun setEventColorAsync(event: CalendarEntity.Event?, color: Int) =
        withContext(Dispatchers.IO) {
            HomeState.courses.firstOrNull()?.let { list ->
                dataManager.colors.insertAll(
                    dataManager.colors.getData().map { colorEntity ->
                        if (colorEntity.title == event?.title) colorEntity.copy(colorInt = color)
                        else colorEntity
                    }
                )
                list.map {
                    if (it.title == event?.title) it.copy(color = color)
                    else it
                }
            }
        }

    private suspend fun getRandomizedColoredEvents(): List<CalendarEntity.Event>? =
        withContext(Dispatchers.IO) {
            HomeState.courses.firstOrNull()?.let { list ->
                val colorMap = mutableMapOf<String?, Int>()

                list.map {
                    it.copy(
                        color = getRandomColor(it.title.toString(), colorMap)
                    )
                }
            }
        }

    private suspend fun List<MessageDto>.saveMessages(save: Boolean) {
        if (save) {
            dataManager.messages.insertAll(
                map { dto ->
                    Message(
                        id = dto.Id,
                        subject = dto.Subject,
                        detail = dto.Detail,
                        senderName = dto.Name,
                        date = dto.SendDate,
                        isNew = dto.IsNew
                    )
                }
            )
        }
    }
}

suspend fun MutableStateFlow<ApiResult<List<MessageDto>>>.getSuccess(): List<MessageDto> {
    return when (val result = this.first()) {
        is ApiResult.Error -> { listOf<MessageDto>()}
        is ApiResult.Progress -> { listOf<MessageDto>()}
        is ApiResult.Success -> {
            result.data
        }
    }
}