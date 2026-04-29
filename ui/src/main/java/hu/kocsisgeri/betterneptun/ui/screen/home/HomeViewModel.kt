package hu.kocsisgeri.betterneptun.ui.screen.home

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.getCourseDateString
import hu.kocsisgeri.betterneptun.common.getTimeLeft
import hu.kocsisgeri.betterneptun.common.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.base.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.home.model.CurrentCourseDetail
import hu.kocsisgeri.betterneptun.ui.screen.home.model.NextCourseDetail
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.getPercent
import hu.kocsisgeri.betterneptun.ui.util.ClockTickReceiver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class HomeViewModel(
    private val clockTickReceiver: ClockTickReceiver,
    private val neptunRepository: NeptunRepository,
    loginRepository: LoginRepository,
) : ComposeViewModel() {

    val refreshProgress = MutableSharedFlow<ApiResult<Unit>>(1, 50)

    val currentCourses = neptunRepository.events.map { list ->
        list.sortedBy { it.startTime }.filter {
            it.startTime.isBefore(LocalDateTime.now()) && it.endTime.isAfter(LocalDateTime.now())
        }
    }.map { currentCourses ->
        currentCourses.map { item  ->
            CurrentCourseDetail(
                title = item.title,
                location = item.location,
                progress = item.getPercent(),
                remainingTime = item.endTime.getTimeLeft(),
                color = item.color
            )
        }
    }.repeatEveryMinute().stateWhileSubscribed(emptyList())

    val nextCourse = neptunRepository.events.map { list ->
        list.sortedBy { it.startTime }.firstOrNull {
            it.startTime.isAfter(LocalDateTime.now())
        }?.let { event ->
            NextCourseDetail(
                title = event.title,
                location = event.location,
                startTime = event.startTime,
                endTime = event.endTime,
                color = event.color,
                timeUntilEvent = event.startTime.getCourseDateString()
            )
        }
    }.repeatEveryMinute().stateWhileSubscribed(null)

    val studentData = loginRepository.studentData
        .filterIsInstance<ApiResult.Success<StudentData>>()
        .map { it.data }
        .stateWhileSubscribed(null)

    val unreadMessages = neptunRepository.unreadMessagesCount

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launchReportingErrors {
            neptunRepository.fetchUnreadMessages()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> Flow<T>.repeatEveryMinute(): Flow<T> {
        return clockTickReceiver.minuteTickFlow.flatMapLatest {
            this // Itt indul újra az eredeti Flow-d
        }
    }
}