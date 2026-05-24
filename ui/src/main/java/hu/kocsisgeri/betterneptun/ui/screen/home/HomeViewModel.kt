package hu.kocsisgeri.betterneptun.ui.screen.home

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.domain.usecase.home.FetchUnreadMessagesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetCurrentCoursesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetNextCourseUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.home.GetStudentDataUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.core.helper.ClockTickReceiver
import hu.kocsisgeri.betterneptun.ui.core.helper.getCourseDateString
import hu.kocsisgeri.betterneptun.ui.core.helper.getTimeLeft
import hu.kocsisgeri.betterneptun.ui.screen.home.model.CurrentCourseDetail
import hu.kocsisgeri.betterneptun.ui.screen.home.model.NextCourseDetail
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.getPercent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest

class HomeViewModel(
    private val clockTickReceiver: ClockTickReceiver,
    private val localizationService: LocalizationService,
    private val fetchUnreadMessagesUseCase: FetchUnreadMessagesUseCase,
    getCurrentCoursesUseCase: GetCurrentCoursesUseCase,
    getNextCourseUseCase: GetNextCourseUseCase,
    getStudentDataUseCase: GetStudentDataUseCase,
) : ComposeViewModel() {

    val refresher = MutableSharedFlow<Unit>(0, 10)
    val refreshProgress = MutableSharedFlow<ApiResult<Unit>>(1, 50)

    val currentCourses = getCurrentCoursesUseCase { item ->
        CurrentCourseDetail(
            title = item.title,
            location = item.location,
            progress = item.getPercent(),
            remainingTime = item.endTime.getTimeLeft { id, args ->
                localizationService.localized(id, *args)
            },
            color = item.color
        )
    }.repeatEveryMinute().stateWhileSubscribed(emptyList())

    val nextCourse = getNextCourseUseCase { event ->
        NextCourseDetail(
            title = event.title,
            location = event.location,
            startTime = event.startTime,
            endTime = event.endTime,
            color = event.color,
            timeUntilEvent = event.startTime.getCourseDateString { id, args ->
                localizationService.localized(id, *args)
            }
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

    fun <T> Flow<T>.repeatEveryMinute(): Flow<T> =
        clockTickReceiver.minuteTickFlow.flatMapLatest { this }
}