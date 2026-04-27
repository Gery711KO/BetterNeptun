package hu.kocsisgeri.betterneptun.domain.repository.neptun

import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.Average
import hu.kocsisgeri.betterneptun.domain.model.ExtendedTerm
import hu.kocsisgeri.betterneptun.domain.model.Message
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.domain.model.Term
import hu.kocsisgeri.betterneptun.domain.model.Subject
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NeptunRepository {

    val events: Flow<List<CalendarEntity.Event>>
    val messages: Flow<List<Message>>
    val unreadMessagesCount : StateFlow<Int?>
    val studentData: StateFlow<ApiResult<StudentData>>

    val extendedTerms : StateFlow<ApiResult<List<ExtendedTerm>>>
    val subjects: StateFlow<ApiResult<List<Subject>>>
    val terms : StateFlow<ApiResult<List<Term>>>
    val averages : StateFlow<ApiResult<List<Average>>>
    var currentMessagePage : Int

    suspend fun fetchMessages()
    suspend fun fetchUnreadMessages()
    suspend fun getMessageDetail(messageId: String): MessageDetail

    suspend fun fetchCalendarData()

    suspend fun fetchExtendedTerms()
    suspend fun fetchSubjects(termId: String)

    suspend fun fetchTerms()
    suspend fun fetchTermAverages()

    suspend fun login(neptunCode: String, password: String)

    suspend fun randomiseCalendarColors()
    suspend fun setEventColor(event: CalendarEntity.Event?, color: Int)

    fun resetMessagePage()
}