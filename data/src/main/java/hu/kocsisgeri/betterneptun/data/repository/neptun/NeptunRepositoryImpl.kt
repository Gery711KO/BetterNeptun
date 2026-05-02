package hu.kocsisgeri.betterneptun.data.repository.neptun

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toAvatarDomain
import hu.kocsisgeri.betterneptun.data.mapper.toAverageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toDomain
import hu.kocsisgeri.betterneptun.data.mapper.toEntity
import hu.kocsisgeri.betterneptun.data.mapper.toExtendedTermDomain
import hu.kocsisgeri.betterneptun.data.mapper.toMessageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toSubjectDomain
import hu.kocsisgeri.betterneptun.data.mapper.toTermDomain
import hu.kocsisgeri.betterneptun.data.model.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.data.repository.runApiCall
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.Avatar
import hu.kocsisgeri.betterneptun.domain.model.Average
import hu.kocsisgeri.betterneptun.domain.model.CalendarItem
import hu.kocsisgeri.betterneptun.domain.model.ExtendedTerm
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail
import hu.kocsisgeri.betterneptun.domain.model.MessagesPager
import hu.kocsisgeri.betterneptun.domain.model.Subject
import hu.kocsisgeri.betterneptun.domain.model.Term
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

internal class NeptunRepositoryImpl(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : NeptunRepository {

    override var currentMessagePage = 1

    private val remoteEvents = MutableStateFlow<List<CalendarItem.Event>>(listOf())
    private val localEvents = localDataSource.localEvents.getData().map { list ->
        list.map { it.toDomain() }
    }

    override val events = combine(remoteEvents, localEvents) { remote, local ->
        remote + local
    }

    override val messages = MutableStateFlow(MessagesPager())
    override val unreadMessagesCount: MutableStateFlow<Int?> = MutableStateFlow(null)

    override val extendedTerms = MutableStateFlow<ApiResult<List<ExtendedTerm>>>(ApiResult.Loading)
    override val subjects =
        MutableStateFlow<ApiResult<List<Subject>>>(ApiResult.Loading)

    override val terms = MutableStateFlow<ApiResult<List<Term>>>(ApiResult.Loading)
    override val averages = MutableStateFlow<ApiResult<List<Average>>>(ApiResult.Loading)

    override suspend fun fetchMessages(isRefresh: Boolean) {
        val pageSize = 20
        if (isRefresh) {
            currentMessagePage = 1
        }

        if (isRefresh.not() && messages.value.isEndReached) return

        val firstRow = (currentMessagePage - 1) * pageSize
        val lastRow = currentMessagePage * pageSize

        withContext(ioDispatcher) {
            try {
                messages.update { pager ->
                    pager.copy(
                        messages = if (isRefresh) emptyList() else pager.messages,
                        isLoadingNextMessages = true,
                        error = null
                    )
                }

                val response = networkDataSource.getReceivedMessages(
                    firstRow = firstRow,
                    lastRow = lastRow
                )

                val avatarsResponse = networkDataSource.getUserAvatars(
                    userIds = response.data.receivedMessages.mapNotNull { it.senderUserId }
                )

                val newMessages = response.data.receivedMessages.map {
                    it.toMessageDomain().copy(
                        senderAvatar = avatarsResponse.data.find { avatar ->
                            avatar.userId == it.senderUserId
                        }?.toAvatarDomain() ?: Avatar.SystemAvatar
                    )
                }

                messages.update { pager ->
                    val currentMessages = pager.messages

                    pager.copy(
                        messages = if (isRefresh) newMessages else currentMessages + newMessages,
                        isLoadingNextMessages = false,
                        isEndReached = newMessages.size < pageSize
                    )
                }

                if (newMessages.isNotEmpty()) {
                    currentMessagePage++
                }
            } catch (exception: Exception) {
                messages.update { pager ->
                    pager.copy(
                        error = exception.message ?: "Something went wrong.",
                        isLoadingNextMessages = false,
                        isEndReached = false
                    )
                }
            }
        }
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

    override suspend fun readMessage(messageId: String, message: MessageDetail) {
        if (message.hasUnreadPost) {
            withContext(ioDispatcher) {
                networkDataSource.postMessagePostRead(
                    messageId = messageId,
                    postIds = PostIdsRequestDto(
                        postIds = message.posts.map { it.id }
                    )
                )
            }
        }
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

    override suspend fun addLocalEvent(event: CalendarItem.LocalEvent) {
        withContext(ioDispatcher) {
            localDataSource.localEvents.insertOne(event.toEntity())
        }
    }

    override suspend fun deleteLocalEvent(eventId: Long) {
        withContext(ioDispatcher) {
            localDataSource.localEvents.deleteById(eventId)
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