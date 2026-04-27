package hu.kocsisgeri.betterneptun.data.repository.neptun

import android.graphics.Color
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toAverageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toExtendedTermDomain
import hu.kocsisgeri.betterneptun.data.mapper.toMessageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toMessageEntity
import hu.kocsisgeri.betterneptun.data.mapper.toSubjectDomain
import hu.kocsisgeri.betterneptun.data.mapper.toTermDomain
import hu.kocsisgeri.betterneptun.data.model.ExtendedTermDto
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.Average
import hu.kocsisgeri.betterneptun.domain.model.ExtendedTerm
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.domain.model.Subject
import hu.kocsisgeri.betterneptun.domain.model.Term
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

internal class NeptunRepositoryImpl(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : NeptunRepository {

    override val events = MutableStateFlow<List<CalendarEntity.Event>>(listOf())

    override val messages = localDataSource.appDatabase.messages.getData().map { list ->
        list.map { it.toMessageDomain() }
    }
    override var currentMessagePage = 1
    override val unreadMessagesCount: MutableStateFlow<Int?> = MutableStateFlow(null)

    override val studentData = MutableStateFlow<ApiResult<StudentData>>(ApiResult.Loading)

    override val extendedTerms = MutableStateFlow<ApiResult<List<ExtendedTerm>>>(ApiResult.Loading)
    override val subjects =
        MutableStateFlow<ApiResult<List<Subject>>>(ApiResult.Loading)

    override val terms = MutableStateFlow<ApiResult<List<Term>>>(ApiResult.Loading)
    override val averages = MutableStateFlow<ApiResult<List<Average>>>(ApiResult.Loading)



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
        networkDataSource.getUnreadMessageCount().let {
            unreadMessagesCount.value = it.data.count
        }
    }

    override suspend fun fetchExtendedTerms() {
        extendedTerms.runApiCall {
            networkDataSource.getExtendedTerms().data.toExtendedTermDomain()
        }
    }

    override suspend fun fetchSubjects(termId: String) {
        subjects.runApiCall {
            networkDataSource.getTakenSubjects(termId).data.toSubjectDomain()
        }
    }

    override suspend fun fetchTerms() {
        terms.runApiCall {
            networkDataSource.getTerms().data.toTermDomain()
        }
    }

    override suspend fun fetchTermAverages() {
        averages.runApiCall {
            networkDataSource.getTermAverages()
                .data
                .termAveragesByTrainings
                .toAverageDomain()
        }
    }

    override suspend fun login(neptunCode: String, password: String) {
        studentData.runApiCall {
            networkDataSource.getUserInfo().data.let {
                StudentData(
                    name = it.name,
                    neptun = it.neptunCode
                )
            }
        }
    }

    override fun resetMessagePage() {
        currentMessagePage = 0
    }

    override suspend fun getMessageDetail(messageId: String) =
        withContext(ioDispatcher) {
            networkDataSource.getMessageDetails(messageId).data.toMessageDomain()
        }

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

    private suspend fun <T : Any> MutableStateFlow<ApiResult<T>>.runApiCall(
        block: suspend () -> T
    ) {
        withContext(ioDispatcher) {
            try {
                value = ApiResult.Loading
                value = ApiResult.Success(block())
            } catch (exception: HttpException) {
                value = ApiResult.Error(exception.message ?: "Network error.")
            } catch (exception: IOException) {
                value = ApiResult.Error(exception.message ?: "Something went wrong.")
            } catch (exception: SerializationException) {
                value = ApiResult.Error(exception.message ?: "Serialization error.")
            }
        }
    }
}