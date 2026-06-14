package hu.kocsisgeri.betterneptun.ui.screen.timetable

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.common.utils.plus
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.usecase.timetable.AddLocalEventUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.timetable.DeleteLocalEventUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.timetable.GetEventsUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.ViewMode
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.toComposeEvent
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateRange
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.nextOrSame
import kotlinx.datetime.previousOrSame
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Duration.Companion.days

@KoinViewModel
class TimetableViewModel(
    @InjectedParam defaultSelected: Long?,
    getEventsUseCase: GetEventsUseCase,
    private val addLocalEventsUseCase: AddLocalEventUseCase,
    private val deleteLocalEventUseCase: DeleteLocalEventUseCase
) : ComposeViewModel() {

    private val maxPageCount = Int.MAX_VALUE
    private val initialPage = maxPageCount / 2
    private val baseDate = LocalDateTime.now()

    private val events = getEventsUseCase()
        .stateWhileSubscribed(emptyList())
    private val currentSelected = MutableStateFlow(defaultSelected)

    private val _viewMode = MutableStateFlow(ViewMode.WEEK)
    val viewMode = _viewMode.stateWhileSubscribed()

    val selectedEvent = combine(
        currentSelected,
        events
    ) { currentSelectedEventId, events ->
        events.find { event ->
            event.id == currentSelectedEventId
        }
    }.stateWhileSubscribed(null)

    fun getWeekDataForPage(page: Int): WeekData {
        val offset = (page - initialPage).toLong()
        val targetDate = when (viewMode.value) {
            ViewMode.WEEK -> baseDate.plus((offset * 7).days)
            ViewMode.DAY -> baseDate.plus(offset.days)
        }

        val dateRange = findDateRange(viewMode.value, targetDate)

        return createWeekData(
            times = "0:23",
            dateRange = dateRange,
            events = events.value
        )
    }

    fun findDateRange(
        mode: ViewMode,
        date: LocalDateTime
    ): LocalDateRange = when (mode) {
        ViewMode.WEEK -> {
            val monday = date.date.previousOrSame(DayOfWeek.MONDAY)
            val friday = monday.nextOrSame(DayOfWeek.SUNDAY)

            LocalDateRange(
                start = monday,
                endInclusive = friday
            )
        }

        ViewMode.DAY -> LocalDateRange(date.date, date.date)
    }

    fun createWeekData(
        times: String,
        dateRange: LocalDateRange,
        events: List<CalendarItem>
    ): WeekData {
        val splitTimes = times.split(":")
        val minHour = splitTimes.getOrNull(0)?.toIntOrNull() ?: 7
        val maxHour = splitTimes.getOrNull(1)?.toIntOrNull() ?: 23

        return WeekData(
            dateRange = dateRange,
            start = LocalTime(minHour, 0),
            end = LocalTime(maxHour, 59)
        ).apply {
            events.filter { it.startTime.date in dateRange }.forEach {
                add(it.toComposeEvent())
            }
        }
    }

    fun selectEvent(eventId: Long?) { currentSelected.value = eventId }
    fun clearSelectedEvent() { currentSelected.tryEmit(null) }
    fun setViewMode(mode: ViewMode) { _viewMode.value = mode }

    fun addEvent(event: CalendarItem.LocalEvent) {
        viewModelScope.launchReportingErrors { addLocalEventsUseCase(event) }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launchReportingErrors {
            deleteLocalEventUseCase(eventId)
            clearSelectedEvent()
        }
    }
}
