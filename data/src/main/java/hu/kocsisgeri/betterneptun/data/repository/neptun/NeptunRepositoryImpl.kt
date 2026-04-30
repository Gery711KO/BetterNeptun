package hu.kocsisgeri.betterneptun.data.repository.neptun

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toAverageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toDomain
import hu.kocsisgeri.betterneptun.data.mapper.toEntity
import hu.kocsisgeri.betterneptun.data.mapper.toExtendedTermDomain
import hu.kocsisgeri.betterneptun.data.mapper.toMessageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toSubjectDomain
import hu.kocsisgeri.betterneptun.data.mapper.toTermDomain
import hu.kocsisgeri.betterneptun.data.repository.runApiCall
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.Average
import hu.kocsisgeri.betterneptun.domain.model.CalendarItem
import hu.kocsisgeri.betterneptun.domain.model.ExtendedTerm
import hu.kocsisgeri.betterneptun.domain.model.Message
import hu.kocsisgeri.betterneptun.domain.model.Subject
import hu.kocsisgeri.betterneptun.domain.model.Term
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class NeptunRepositoryImpl(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : NeptunRepository {

    override var currentMessagePage = 1

    private val remoteEvents = MutableStateFlow<List<CalendarItem.Event>>(listOf())
    private val localEvents = localDataSource.appDatabase.localEvents.getData().map { list ->
        list.map { it.toDomain() }
    }

    override val events = combine(remoteEvents, localEvents) { remote, local ->
        remote + local
    }

    override val messages = MutableStateFlow<ApiResult<List<Message>>>(ApiResult.Loading)
    override val unreadMessagesCount: MutableStateFlow<Int?> = MutableStateFlow(null)

    override val extendedTerms = MutableStateFlow<ApiResult<List<ExtendedTerm>>>(ApiResult.Loading)
    override val subjects =
        MutableStateFlow<ApiResult<List<Subject>>>(ApiResult.Loading)

    override val terms = MutableStateFlow<ApiResult<List<Term>>>(ApiResult.Loading)
    override val averages = MutableStateFlow<ApiResult<List<Average>>>(ApiResult.Loading)

    override suspend fun fetchMessages() {
        messages.runApiCall(ioDispatcher) {
            networkDataSource.getReceivedMessages(
                firstRow = 0,
                lastRow = currentMessagePage * 20
            ).let { response ->
                response.data.receivedMessages.map {
                    it.toMessageDomain()
                }
            }
        }

        currentMessagePage + 1
    }

    override suspend fun fetchUnreadMessages() {
        networkDataSource.getUnreadMessageCount().let {
            unreadMessagesCount.value = it.data.count
        }
    }

    override suspend fun fetchExtendedTerms() {
        extendedTerms.runApiCall(ioDispatcher) {
            networkDataSource.getExtendedTerms().data.toExtendedTermDomain()
        }
    }

    override suspend fun fetchSubjects(termId: String) {
        subjects.runApiCall(ioDispatcher) {
            networkDataSource.getTakenSubjects(termId).data.toSubjectDomain()
        }
    }

    override suspend fun fetchTerms() {
        terms.runApiCall(ioDispatcher) {
            networkDataSource.getTerms().data.toTermDomain()
        }
    }

    override suspend fun fetchTermAverages() {
        averages.runApiCall(ioDispatcher) {
            networkDataSource.getTermAverages()
                .data
                .termAveragesByTrainings
                .toAverageDomain()
        }
    }

    override suspend fun getMessageDetail(messageId: String) =
        withContext(ioDispatcher) {
            networkDataSource.getMessageDetails(messageId).data.toMessageDomain()
        }

    override fun purge() {
        remoteEvents.value = emptyList()
        unreadMessagesCount.value = null
        extendedTerms.value = ApiResult.Loading
        subjects.value = ApiResult.Loading
        terms.value = ApiResult.Loading
        averages.value = ApiResult.Loading
        currentMessagePage = 1
    }

    override suspend fun randomiseCalendarColors() {
//        withContext(ioDispatcher) {
//            getRandomizedColoredEvents()?.let {
//                events.tryEmit(it)
//                CourseRepository.courses.tryEmit(it)
//            }
//        }
    }

    override suspend fun setLocalEventColor(event: CalendarItem.LocalEvent, color: Int) {
        withContext(ioDispatcher) {
            val mappedEvents = localDataSource.appDatabase.localEvents.getData().map { events ->
                events.map {
                    if (it.id == event.id) {
                        it.toDomain().copy(
                            color = color
                        ).toEntity()
                    } else {
                        it
                    }
                }
            }.first()

            localDataSource.appDatabase.localEvents.insertAll(mappedEvents)
        }
    }

    override suspend fun addLocalEvent(event: CalendarItem.LocalEvent) {
        withContext(ioDispatcher) {
            localDataSource.appDatabase.localEvents.insertOne(event.toEntity())
        }
    }

    override suspend fun deleteLocalEvent(eventId: Long) {
        withContext(ioDispatcher) {
            localDataSource.appDatabase.localEvents.deleteById(eventId)
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
//                        remoteEvents.tryEmit(event)
//                    }
//                }
//            }
    }
}