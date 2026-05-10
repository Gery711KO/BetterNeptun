package hu.kocsisgeri.betterneptun.domain.repository.neptun

import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.Average
import hu.kocsisgeri.betterneptun.domain.model.CalendarItem
import hu.kocsisgeri.betterneptun.domain.model.ExtendedTerm
import hu.kocsisgeri.betterneptun.domain.model.Message
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail
import hu.kocsisgeri.betterneptun.domain.model.MessagesPager
import hu.kocsisgeri.betterneptun.domain.model.Term
import hu.kocsisgeri.betterneptun.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NeptunRepository {

    val events: Flow<List<CalendarItem>>
    val messages: StateFlow<MessagesPager>
    val unreadMessagesCount : StateFlow<Int?>

    val extendedTerms : StateFlow<ApiResult<List<ExtendedTerm>>>
    val subjects: StateFlow<ApiResult<List<Subject>>>
    val terms : StateFlow<ApiResult<List<Term>>>
    val averages : StateFlow<ApiResult<List<Average>>>
    var currentMessagePage : Int

    suspend fun checkForMessageUpdates()
    suspend fun fetchMessages(isRefresh: Boolean = false)
    suspend fun fetchUnreadMessages()
    suspend fun getMessageDetail(messageId: String): MessageDetail
    suspend fun readMessage(messageId: String, message: MessageDetail)

    suspend fun fetchCalendarData()

    suspend fun fetchExtendedTerms()
    suspend fun fetchSubjects(termId: String)

    suspend fun fetchTerms()
    suspend fun fetchTermAverages()

    suspend fun addLocalEvent(event: CalendarItem.LocalEvent)
    suspend fun deleteLocalEvent(eventId: Long)

    fun purge()
}