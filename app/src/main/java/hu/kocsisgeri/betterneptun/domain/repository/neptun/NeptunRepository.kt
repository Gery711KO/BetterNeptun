package hu.kocsisgeri.betterneptun.domain.repository.neptun

import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.model.ReceivedMessageDto
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.ui.model.MarkBookDataModel
import hu.kocsisgeri.betterneptun.ui.model.SemesterModel
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NeptunRepository {
    val events : Flow<List<CalendarEntity.Event>>
    val messages : StateFlow<ApiResult<List<ReceivedMessageDto>>>
    val unreadMessagesCount : StateFlow<Int?>
    val studentData: StateFlow<StudentData?>
    val markBookData: StateFlow<ApiResult<List<MarkBookDataModel>>>
    val averages : StateFlow<ApiResult<List<SemesterModel>>>
    var currentMessagePage : Int
    fun fetchMessages()
    fun fetchUnreadMessages()
    fun fetchCalendarData()
    fun fetchMarkBookData()
    fun fetchAverages()
    suspend fun login(neptunCode: String, password: String) : ApiResult<StudentData>
    fun resetMessagePage()
    fun randomiseCalendarColors()
    fun setEventColor(event: CalendarEntity.Event?, color: Int)
    fun readMessage(messageId: Int)
    fun setStudentData(studentData: StudentData?)
}