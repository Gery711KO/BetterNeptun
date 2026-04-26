package hu.kocsisgeri.betterneptun.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import hu.kocsisgeri.betterneptun.utils.launchReportingErrors
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {

    val currentCourses = MutableStateFlow<List<CalendarEntity.Event>>(emptyList())
    val nextCourse = MutableStateFlow<ApiResult<CalendarEntity.Event>?>(null)

    val refreshProgress = MutableSharedFlow<ApiResult<Unit>>(1, 50)

    val studentData = neptunRepository.studentData
    val unreadMessages = neptunRepository.unreadMessagesCount

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launchReportingErrors {
            neptunRepository.fetchUnreadMessages()
        }
    }
}