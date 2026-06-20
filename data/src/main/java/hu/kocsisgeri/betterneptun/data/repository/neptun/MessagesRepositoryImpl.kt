package hu.kocsisgeri.betterneptun.data.repository.neptun

import hu.kocsisgeri.betterneptun.core.network.model.neptun.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toAvatarDomain
import hu.kocsisgeri.betterneptun.data.mapper.toMessageDomain
import hu.kocsisgeri.betterneptun.data.util.runApiCall
import hu.kocsisgeri.betterneptun.domain.clearable.BaseClearable
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Avatar
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessageDetail
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessagesPager
import hu.kocsisgeri.betterneptun.domain.repository.neptun.MessagesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton
internal class MessagesRepositoryImpl internal constructor(
    private val networkDataSource: NetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : MessagesRepository, BaseClearable() {

    override var currentMessagePage = 1

    override val messages = clearableStateFlow(MessagesPager())
    override val unreadMessagesCount: MutableStateFlow<ApiResult<Int>> = clearableStateFlow(ApiResult.Loading)

    override suspend fun checkForMessageUpdates() {
        withContext(ioDispatcher) {
            fetchUnreadMessages()
            val newMessages = (unreadMessagesCount.value as? ApiResult.Success)?.data ?: 0

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

                val userIds = response.data.receivedMessages.mapNotNull { it.senderUserId }.distinct()
                val avatarsResponse = if (userIds.isNotEmpty()) {
                    networkDataSource.getUserAvatars(userIds = userIds)
                } else null

                val details = response.data.receivedMessages.map {
                    async { it.messageId to getMessageDetail(it.messageId) }
                }.awaitAll()

                val newMessages = response.data.receivedMessages.map { message ->
                    val senderAvatar = avatarsResponse?.data?.find { avatar ->
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
        unreadMessagesCount.runApiCall(dispatcher = ioDispatcher) {
            networkDataSource.getUnreadMessageCount().data.count
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

    override suspend fun onClear() {
        currentMessagePage = 1
    }
}
