package hu.kocsisgeri.betterneptun.domain.repository.neptun

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Average
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessageDetail
import hu.kocsisgeri.betterneptun.domain.model.neptun.MessagesPager
import hu.kocsisgeri.betterneptun.domain.model.neptun.Term
import hu.kocsisgeri.betterneptun.domain.model.neptun.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for managing Neptun-related data and operations.
 * Handles messages, calendar events, subjects, and academic performance data.
 */
interface NeptunRepository {

    /**
     * The index of the currently loaded message page.
     */
    val currentMessagePage : Int

    /**
     * A [Flow] of calendar items, including both Neptun events and user-defined local events.
     */
    val events: Flow<List<CalendarItem>>

    /**
     * A [StateFlow] providing the current [MessagesPager] state for paginated message lists.
     */
    val messages: StateFlow<MessagesPager>

    /**
     * A [StateFlow] representing the current number of unread messages.
     * Null if the count hasn't been fetched yet.
     */
    val unreadMessagesCount : StateFlow<Int?>

    /**
     * A [StateFlow] containing the result of fetching subjects for the selected term.
     */
    val subjects: StateFlow<ApiResult<List<Subject>>>

    /**
     * A [StateFlow] containing the list of available academic terms.
     */
    val terms : StateFlow<ApiResult<List<Term>>>

    /**
     * A [StateFlow] containing grade averages across different terms.
     */
    val averages : StateFlow<ApiResult<List<Average>>>

    /**
     * Checks for new message updates from the server.
     */
    suspend fun checkForMessageUpdates()

    /**
     * Fetches a page of messages.
     * @param isRefresh If true, resets the pager and fetches the first page.
     */
    suspend fun fetchMessages(isRefresh: Boolean = false)

    /**
     * Fetches the current unread message count.
     */
    suspend fun fetchUnreadMessages()

    /**
     * Retrieves the detailed content of a specific message.
     * @param messageId The unique identifier of the message.
     * @return The [MessageDetail] containing the message body and metadata.
     */
    suspend fun getMessageDetail(messageId: String): MessageDetail

    /**
     * Marks a message as read.
     * @param messageId The unique identifier of the message.
     * @param message The detail of the message to be marked as read.
     */
    suspend fun readMessage(messageId: String, message: MessageDetail)

    /**
     * Fetches calendar data (events) from Neptun for the current period.
     */
    suspend fun fetchCalendarData()

    /**
     * Fetches the list of subjects for a specific term.
     * @param termId The unique identifier of the term.
     */
    suspend fun fetchSubjects(termId: String)

    /**
     * Fetches the list of available academic terms.
     */
    suspend fun fetchTerms()

    /**
     * Fetches the academic averages for all available terms.
     */
    suspend fun fetchTermAverages()

    /**
     * Adds a user-defined local event to the calendar.
     * @param event The [CalendarItem.LocalEvent] to add.
     */
    suspend fun addLocalEvent(event: CalendarItem.LocalEvent)

    /**
     * Deletes a user-defined local event from the calendar.
     * @param eventId The unique identifier of the local event to delete.
     */
    suspend fun deleteLocalEvent(eventId: Long)
}
