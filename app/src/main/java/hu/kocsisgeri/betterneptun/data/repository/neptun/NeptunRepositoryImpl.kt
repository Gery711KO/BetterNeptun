package hu.kocsisgeri.betterneptun.data.repository.neptun

import android.graphics.Color
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toMessageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toMessageEntity
import hu.kocsisgeri.betterneptun.data.model.ReceivedMessageDto
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.model.MarkBookDataModel
import hu.kocsisgeri.betterneptun.ui.model.SemesterModel
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class NeptunRepositoryImpl(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : NeptunRepository {

    override val events = MutableStateFlow<List<CalendarEntity.Event>>(listOf())
    override val messages = localDataSource.appDatabase.messages.getData().map { list ->
        list.map { it.toMessageDomain() }
    }
    override val studentData = MutableStateFlow<StudentData?>(null)
    override val markBookData =
        MutableStateFlow<ApiResult<List<MarkBookDataModel>>>(ApiResult.Progress(0))
    override val averages = MutableStateFlow<ApiResult<List<SemesterModel>>>(ApiResult.Progress(0))
    override var currentMessagePage = 0

    override val unreadMessagesCount: MutableStateFlow<Int?> = MutableStateFlow(null)

    override suspend fun fetchMessages() {
        withContext(ioDispatcher) {
            networkDataSource.getReceivedMessages(
                firstRow = 0,
                lastRow = 20
            ).let {
                localDataSource.appDatabase.messages.insertAll(
                    it.data.receivedMessages.map { message ->
                        message.toMessageEntity()
                    }
                )
            }
        }
    }

    override suspend fun fetchUnreadMessages() {
        withContext(ioDispatcher){
            networkDataSource.getUnreadMessageCount().let {
                unreadMessagesCount.value = it.data.count
            }
        }
    }

    override suspend fun fetchCalendarData() {
//            val response = networkDataSource.getCourses()
//            val colorMap = mutableMapOf<String?, Int>()
//            when (response) {
//                is ApiResult.Error -> {/* do something about errors */ }
//                is ApiResult.Progress -> {/* don't need to do anything here */ }
//                is ApiResult.Success -> {
//                    response.data.events.filter { event ->
//                        event.allday != 1
//                    }.map {
//                        CalendarEntity.Event(
//                            it.id?.toLong() ?: 1111111,
//                            title = it.title?.split("]")?.get(1)?.split("(")?.get(0) ?: "ERROR",
//                            startTime = it.startdate?.split("(")?.get(1)?.split(")")?.get(0)
//                                ?.toLong()
//                                ?.let { longTime ->
//                                    LocalDateTime.ofEpochSecond(
//                                        longTime / 1000,
//                                        0,
//                                        ZoneOffset.UTC
//                                    )
//                                }
//                                ?: LocalDateTime.now(),
//                            endTime = it.enddate?.split("(")?.get(1)?.split(")")?.get(0)?.toLong()
//                                ?.let { longTime ->
//                                    LocalDateTime.ofEpochSecond(
//                                        longTime / 1000,
//                                        0,
//                                        ZoneOffset.UTC
//                                    )
//                                }
//                                ?: LocalDateTime.now(),
//                            location = it.location.toString(),
//                            color = getRandomColor(
//                                it.title?.split("]")?.get(1)?.split("(")?.get(0) ?: "ERROR",
//                                colorMap
//                            ),
//                            isAllDay = it.allday != 0,
//                            isCanceled = false,
//                            subjectCode = it.title?.split("(")?.get(1)?.split(")")?.get(0)
//                                ?: "ERROR",
//                            courseCode = it.title?.split(" - ")?.get(1)?.split(" ")?.get(0)
//                                ?: "ERROR",
//                            teacher = it.title?.split("(")?.get(2)?.split(")")?.get(0) ?: "ERROR"
//                        )
//                    }.let { event ->
//                        dataManager.colors.insertAll(event.map {
//                            hu.kocsisgeri.betterneptun.data.dao.Color(
//                                title = it.title.toString(),
//                                colorInt = it.color
//                            )
//                        })
//                        HomeState.courses.tryEmit(event)
//                        events.tryEmit(event)
//                    }
//                }
//            }
    }

    override suspend fun fetchMarkBookData() {
//            currentUser.first().let { user ->
//                networkDataSource.getAddedCourses(user).check { subject ->
//                    networkDataSource.getMarkBookData(user).check { mark ->
//                        val data = subject.AddedSubjectsList.map {
//                            MarkBookDataModel(
//                                subjectId = it.SubjectID,
//                                subjectCode = it.SubjectCode,
//                                subjectCredit = it.SubjectCredit,
//                                subjectName = it.SubjectName,
//                                subjectRequirement = it.SubjectRequirement,
//                                subjectType = it.SubjectType,
//                                termId = it.TermId,
//                                completed = false,
//                                signer = "",
//                                values = "",
//                                state = SubjectState.DEFAULT
//                            )
//                        }.map { markData ->
//                            markData.copy(
//                                completed = mark.MarkBookList.firstOrNull {
//                                    it.SubjectName == markData.subjectName
//                                }?.Completed?:false,
//                                signer = mark.MarkBookList.firstOrNull {
//                                    it.SubjectName == markData.subjectName
//                                }?.Signer?: "",
//                                values = mark.MarkBookList.firstOrNull {
//                                    it.SubjectName == markData.subjectName
//                                }?.Values?: ""
//                            )
//                        }.map { markData ->
//                            markData.copy(
//                                state = getSubjectState(markData.completed, markData.signer)
//                            )
//                        }
//                        markBookData.tryEmit(ApiResult.Success(data))
//                    }
//                }
//            }
    }

    override suspend fun fetchAverages() {
//            networkDataSource.getAverages().let {
//                if (it.isNotEmpty()) averages.tryEmit(ApiResult.Success(it))
//                else averages.tryEmit(ApiResult.Error("Network error"))
//            }
    }

    override suspend fun login(neptunCode: String, password: String): ApiResult<StudentData> =
        withContext(ioDispatcher) {
            networkDataSource.getUserInfo().let { result ->
                ApiResult.Success(
                    StudentData(
                        name = result.data.name,
                        neptun = result.data.neptunCode
                    )
                )
            }
        }

    private suspend fun getRandomColor(title: String?, colorMap: MutableMap<String?, Int>): Int {
        val colors = localDataSource.appDatabase.colors.getData().firstOrNull()
        val current = colors?.firstOrNull { it.title == title }

        val random = IntRange(0, 255)

        val baseColor = Color.BLACK;

        val baseRed = Color.red(baseColor);
        val baseGreen = Color.green(baseColor);
        val baseBlue = Color.blue(baseColor);

        val red = (baseRed + random.random()) / 2;
        val green = (baseGreen + random.random()) / 2;
        val blue = (baseBlue + random.random()) / 2;

        return if (colors.isNullOrEmpty()) {
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

    override suspend fun getMessageDetail(messageId: String) =
        networkDataSource.getMessageDetails(messageId).data.toMessageDomain()

    override suspend fun randomiseCalendarColors() {
//        withContext(ioDispatcher) {
//            getRandomizedColoredEvents()?.let {
//                events.tryEmit(it)
//                CourseRepository.courses.tryEmit(it)
//            }
//        }
    }

    override suspend fun setEventColor(event: CalendarEntity.Event?, color: Int) {
//        withContext(ioDispatcher) {
//            setEventColorAsync(event, color)?.let {
//                events.tryEmit(it)
//                CourseRepository.courses.tryEmit(it)
//            }
//        }
    }

    override fun setStudentData(studentData: StudentData?) {
        this.studentData.value = studentData
    }

    override fun resetMessagePage() {
        currentMessagePage = 0
    }
}