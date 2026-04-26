package hu.kocsisgeri.betterneptun.domain.repository.neptun

import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.Message
import hu.kocsisgeri.betterneptun.domain.model.MessageDetail
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.ui.model.MarkBookDataModel
import hu.kocsisgeri.betterneptun.ui.model.SemesterModel
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NeptunRepository {

    val events: Flow<List<CalendarEntity.Event>>
    val messages: Flow<List<Message>>
    val unreadMessagesCount : StateFlow<Int?>
    val studentData: StateFlow<StudentData?>
    val markBookData: StateFlow<ApiResult<List<MarkBookDataModel>>>
    val averages : StateFlow<ApiResult<List<SemesterModel>>>
    var currentMessagePage : Int

    suspend fun fetchMessages()
    suspend fun fetchUnreadMessages()
    suspend fun fetchCalendarData()
    suspend fun fetchMarkBookData()
    suspend fun fetchAverages()
    suspend fun login(neptunCode: String, password: String) : ApiResult<StudentData>
    suspend fun randomiseCalendarColors()
    suspend fun setEventColor(event: CalendarEntity.Event?, color: Int)
    suspend fun getMessageDetail(messageId: String): MessageDetail

    fun resetMessagePage()
    fun setStudentData(studentData: StudentData?)
}