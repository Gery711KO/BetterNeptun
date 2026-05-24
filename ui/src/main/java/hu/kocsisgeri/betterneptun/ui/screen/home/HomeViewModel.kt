package hu.kocsisgeri.betterneptun.ui.screen.home

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.usecase.home.FetchUnreadMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetCurrentCoursesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetNextCourseUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetStudentDataUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.core.helper.ClockMinutesTickReceiver
import hu.kocsisgeri.betterneptun.ui.screen.home.model.CurrentCourseDetail
import hu.kocsisgeri.betterneptun.ui.screen.home.model.NextCourseDetail
import hu.kocsisgeri.betterneptun.ui.screen.home.model.getTimeLeft
import hu.kocsisgeri.betterneptun.ui.screen.home.model.getTimeUntil
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.getPercent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val clockTickReceiver: ClockMinutesTickReceiver,
    private val fetchUnreadMessagesUseCase: FetchUnreadMessagesUseCase,
    getCurrentCoursesUseCase: GetCurrentCoursesUseCase,
    getNextCourseUseCase: GetNextCourseUseCase,
    getStudentDataUseCase: GetStudentDataUseCase,
) : ComposeViewModel() {

    val refresher = MutableSharedFlow<Unit>(0, 10)
    val refreshProgress = MutableSharedFlow<ApiResult<Unit>>(1, 50)

    val currentCourses = getCurrentCoursesUseCase { event ->
        CurrentCourseDetail(
            id = event.id,
            title = event.title,
            location = event.location,
            progress = event.getPercent(),
            remainingTimeMinutes = event.endTime.getTimeLeft(),
            color = event.color
        )
    }.repeatEveryMinute().stateWhileSubscribed(emptyList())

    val nextCourse = getNextCourseUseCase { event ->
        NextCourseDetail(
            id = event.id,
            title = event.title,
            location = event.location,
            startTime = event.startTime,
            endTime = event.endTime,
            color = event.color,
            timeUntilEvent = event.startTime.getTimeUntil()
        )
    }.repeatEveryMinute().stateWhileSubscribed(null)

    val studentData = getStudentDataUseCase().stateWhileSubscribed()

    val unreadMessages = refresher.flatMapLatest {
        fetchUnreadMessagesUseCase()
    }.stateWhileSubscribed(null)

    init {
        refreshData()
    }

    fun refreshData() {
        refresher.tryEmit(Unit)
    }

    private fun <T> Flow<T>.repeatEveryMinute(): Flow<T> =
        clockTickReceiver.minuteTick.flatMapLatest { this }
}
