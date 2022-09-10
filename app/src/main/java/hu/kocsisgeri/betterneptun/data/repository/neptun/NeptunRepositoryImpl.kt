package hu.kocsisgeri.betterneptun.data.repository.neptun

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.dao.Message
import hu.kocsisgeri.betterneptun.data.dao.MessageDao
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.domain.api.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.domain.api.dto.MessageDto
import hu.kocsisgeri.betterneptun.domain.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.ui.model.NeptunUser
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import hu.kocsisgeri.betterneptun.utils.PREF_STAY_LOGGED_ID
import hu.kocsisgeri.betterneptun.utils.data_manager.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

class NeptunRepositoryImpl(
    override val coroutineContext: CoroutineContext = Dispatchers.IO,
    private val networkDataSource: NetworkDataSource,
    private val dao: MessageDao,
    private val dataManager: DataManager,
) : NeptunRepository, CoroutineScope {

    override val events = MutableStateFlow<List<CalendarEntity.Event>>(listOf())
    override val messages = MutableStateFlow<ApiResult<List<MessageDto>>>(ApiResult.Progress(0))
    override val currentUser = MutableStateFlow(NeptunUser())
    override var currentMessagePage = 0

    override fun fetchMessages() {
        val save = dataManager.getDefault(PREF_STAY_LOGGED_ID, false)

        launch {
            dao.getData().let { cache ->
                val list = mutableListOf<MessageDto>()
                val maxId = cache.maxByOrNull { it.id }?.id ?: 0
                var counter = 1f
                currentUser.first().let { user ->
                    networkDataSource.getMessages(user.copy(CurrentPage = 0)).let { result ->
                        when (result) {
                            is NetworkResponse.Failure<*> -> 0
                            is NetworkResponse.Success -> result.data.TotalRowCount
                        }
                    }?.let {
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
                                                        CourseRepo.unreadMessages.tryEmit(unread)
                                                    }
                                                    rList.filter { message -> message.Id > maxId }.let { filtered ->
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
                                                        CourseRepo.unreadMessages.tryEmit(unread)
                                                    }
                                                    rList.filter { message -> message.Id > maxId }.let { filtered ->
                                                        if (filtered.isEmpty()) {
                                                            messages.emit(ApiResult.Success(cache.map { mes ->
                                                                MessageDto(
                                                                    Id = mes.id,
                                                                    Detail = mes.detail,
                                                                    Name = mes.senderName,
                                                                    Subject = mes.subject,
                                                                    SendDate = mes.date,
                                                                    IsNew = mes.isNew
                                                                )
                                                            }
                                                                .sortedByDescending { message -> message.Id }))
                                                            return@launch
                                                        } else {
                                                            list.addAll(filtered)
                                                            filtered.saveMessages(save)
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
            val mapped = response.events.map {
                CalendarEntity.Event(
                    it.id?.toLong() ?: 1111111,
                    title = it.title?.split("]")?.get(1)?.split("(")?.get(0) ?: "ERROR",
                    it.startdate?.split("(")?.get(1)?.split(")")?.get(0)?.toLong()
                        ?.let { longTime ->
                            LocalDateTime.ofEpochSecond(
                                longTime / 1000,
                                0,
                                ZoneOffset.UTC
                            )
                        }
                        ?: LocalDateTime.now(),
                    it.enddate?.split("(")?.get(1)?.split(")")?.get(0)?.toLong()
                        ?.let { longTime ->
                            LocalDateTime.ofEpochSecond(
                                longTime / 1000,
                                0,
                                ZoneOffset.UTC
                            )
                        }
                        ?: LocalDateTime.now(),
                    it.location.toString(),
                    getRandomColor(
                        it.title?.split("]")?.get(1)?.split("(")?.get(0) ?: "ERROR",
                        colorMap
                    ),
                    isAllDay = it.allday != 0,
                    false
                )
            }
            mapped.filter { event ->
                !event.isAllDay
            }.let {
                it.let { event ->
                    CourseRepo.courses.tryEmit(event)
                    events.tryEmit(event)
                }
            }
        }
    }

    private fun getRandomColor(title: String?, colorMap: MutableMap<String?, Int>): Int {
        val random = IntRange(0, 255)

        val baseColor = Color.BLACK;

        val baseRed = Color.red(baseColor);
        val baseGreen = Color.green(baseColor);
        val baseBlue = Color.blue(baseColor);

        val red = (baseRed + random.random()) / 2;
        val green = (baseGreen + random.random()) / 2;
        val blue = (baseBlue + random.random()) / 2;

        return if (colorMap.containsKey(title)) {
            colorMap[title]!!
        } else {
            colorMap[title] = Color.rgb(red, green, blue)
            colorMap[title]!!
        }
    }



    override fun resetMessagePage() {
        currentMessagePage = 0
    }

    override fun randomiseCalendarColors() {
        launch {
            getRandomizedColoredEvents()?.let {
                events.tryEmit(it)
                CourseRepo.courses.tryEmit(it)
            }
        }
    }

    private suspend fun getRandomizedColoredEvents() : List<CalendarEntity.Event>? = withContext(Dispatchers.IO) {
        CourseRepo.courses.firstOrNull()?.let {  list ->
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
            dao.insertAll(
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