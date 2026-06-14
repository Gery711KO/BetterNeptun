package hu.kocsisgeri.betterneptun.domain.repository.neptun

import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessageDetail
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessagesPager
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository responsible for managing Neptun messages, including inbox pagination and message details.
 */
interface MessagesRepository {
    /**
     * The index of the currently loaded message page.
     */
    val currentMessagePage: Int

    /**
     * A [StateFlow] emitting the current [MessagesPager] containing the list of messages and pagination state.
     */
    val messages: StateFlow<MessagesPager>

    /**
     * A [StateFlow] emitting the number of unread messages. Emits `null` if the count is unknown.
     */
    val unreadMessagesCount: StateFlow<ApiResult<Int>>

    /**
     * Checks for new message updates from the remote source.
     */
    suspend fun checkForMessageUpdates()

    /**
     * Fetches a page of messages from the inbox.
     *
     * @param isRefresh True if the message list should be refreshed from the first page, false to load the next page.
     */
    suspend fun fetchMessages(isRefresh: Boolean = false)

    /**
     * Fetches the latest count of unread messages.
     */
    suspend fun fetchUnreadMessages()

    /**
     * Retrieves the detailed content of a specific message.
     *
     * @param messageId The unique identifier of the message.
     * @return The [MessageDetail] containing the message body and other metadata.
     */
    suspend fun getMessageDetail(messageId: String): MessageDetail

    /**
     * Marks a message as read and updates its content.
     *
     * @param messageId The unique identifier of the message.
     * @param message The [MessageDetail] to be updated/persisted as read.
     */
    suspend fun readMessage(messageId: String, message: MessageDetail)
}
