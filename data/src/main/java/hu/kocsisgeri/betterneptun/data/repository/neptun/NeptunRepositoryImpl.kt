package hu.kocsisgeri.betterneptun.data.repository.neptun

import hu.kocsisgeri.betterneptun.core.network.model.neptun.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toAvatarDomain
import hu.kocsisgeri.betterneptun.data.mapper.toAverageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toDomain
import hu.kocsisgeri.betterneptun.data.mapper.toEntity
import hu.kocsisgeri.betterneptun.data.mapper.toMessageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toSubjectDomain
import hu.kocsisgeri.betterneptun.data.mapper.toTermDomain
import hu.kocsisgeri.betterneptun.data.util.runApiCall
import hu.kocsisgeri.betterneptun.domain.clearable.BaseClearable
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Avatar
import hu.kocsisgeri.betterneptun.domain.model.neptun.Average
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessageDetail
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessagesPager
import hu.kocsisgeri.betterneptun.domain.model.neptun.Subject
import hu.kocsisgeri.betterneptun.domain.model.neptun.Term
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton
class NeptunRepositoryImpl internal constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : NeptunRepository, BaseClearable() {

    override var currentMessagePage = 1

    private val remoteEvents = clearableStateFlow<List<CalendarItem.Event>>(listOf())
    private val localEvents = localDataSource.localEventsDb.getData().map { list ->
        list.map { it.toDomain() }
    }

    override val events = combine(remoteEvents, localEvents) { remote, local ->
        remote + local
    }

    override val messages = clearableStateFlow(MessagesPager())
    override val unreadMessagesCount: MutableStateFlow<Int?> = clearableStateFlow(null)

    override val subjects =
        clearableStateFlow<ApiResult<List<Subject>>>(ApiResult.Loading)

    override val terms = clearableStateFlow<ApiResult<List<Term>>>(ApiResult.Loading)
    override val averages = clearableStateFlow<ApiResult<List<Average>>>(ApiResult.Loading)

    override suspend fun checkForMessageUpdates() {
        withContext(ioDispatcher) {
            fetchUnreadMessages()
            val newMessages = unreadMessagesCount.value?: 0

            if (newMessages > 0) fetchMessages(isRefresh = true)
        }
    }

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

                val details = response.data.receivedMessages.map {
                    async { it.messageId to getMessageDetail(it.messageId) }
                }.awaitAll()

                val newMessages = response.data.receivedMessages.map { message ->
                    val senderAvatar = avatarsResponse.data.find { avatar ->
                        avatar.userId == message.senderUserId
                    }?.toAvatarDomain() ?: Avatar.SystemAvatar

                    message.toMessageDomain().copy(
                        senderAvatar = senderAvatar,
                        messageDetail = details.find { it.first == message.messageId }?.second
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
            val foundMessage = messages.value.messages.find { it.id == messageId }

            messages.update { pager ->
                pager.copy(
                    messages = pager.messages.toMutableList().apply {
                        foundMessage?.let {
                            val index = indexOf(foundMessage)
                            if (index != -1) this[index] = foundMessage.copy(isNew = false)
                        }
                    }
                )
            }

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

    override suspend fun addLocalEvent(event: CalendarItem.LocalEvent) {
        withContext(ioDispatcher) {
            localDataSource.localEventsDb.insertOne(event.toEntity())
        }
    }

    override suspend fun deleteLocalEvent(eventId: Long) {
        withContext(ioDispatcher) {
            localDataSource.localEventsDb.deleteById(eventId)
        }
    }

    override suspend fun fetchCalendarData() {
        // TODO
    }

    override suspend fun onClear() {
        currentMessagePage = 1
    }
}