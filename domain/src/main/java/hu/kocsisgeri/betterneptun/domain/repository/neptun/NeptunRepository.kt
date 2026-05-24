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

interface NeptunRepository {

    val currentMessagePage : Int

    val events: Flow<List<CalendarItem>>
    val messages: StateFlow<MessagesPager>
    val unreadMessagesCount : StateFlow<Int?>

    val subjects: StateFlow<ApiResult<List<Subject>>>
    val terms : StateFlow<ApiResult<List<Term>>>
    val averages : StateFlow<ApiResult<List<Average>>>

    suspend fun checkForMessageUpdates()
    suspend fun fetchMessages(isRefresh: Boolean = false)
    suspend fun fetchUnreadMessages()
    suspend fun getMessageDetail(messageId: String): MessageDetail
    suspend fun readMessage(messageId: String, message: MessageDetail)

    suspend fun fetchCalendarData()

    suspend fun fetchSubjects(termId: String)

    suspend fun fetchTerms()
    suspend fun fetchTermAverages()

    suspend fun addLocalEvent(event: CalendarItem.LocalEvent)
    suspend fun deleteLocalEvent(eventId: Long)

    fun purge()
}
