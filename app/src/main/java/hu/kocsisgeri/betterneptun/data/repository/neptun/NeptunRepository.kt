package hu.kocsisgeri.betterneptun.data.repository.neptun

import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.domain.api.dto.MessageDto
import hu.kocsisgeri.betterneptun.ui.model.MarkBookDataModel
import hu.kocsisgeri.betterneptun.ui.model.NeptunUser
import hu.kocsisgeri.betterneptun.ui.model.SemesterModel
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface NeptunRepository {
    val events : Flow<List<CalendarEntity.Event>>
    val messages : MutableStateFlow<ApiResult<List<MessageDto>>>
    val currentUser : MutableStateFlow<NeptunUser>
    val markBookData: MutableStateFlow<ApiResult<List<MarkBookDataModel>>>
    val averages : MutableStateFlow<ApiResult<List<SemesterModel>>>
    var currentMessagePage : Int
    fun fetchMessages()
    fun fetchCalendarData()
    fun fetchMarkBookData()
    fun fetchAverages()
    fun resetMessagePage()
    fun randomiseCalendarColors()
    fun setEventColor(event: CalendarEntity.Event?, color: Int)
    fun readMessage(messageId: Int)
}