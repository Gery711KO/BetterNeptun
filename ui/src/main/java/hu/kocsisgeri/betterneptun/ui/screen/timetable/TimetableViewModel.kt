package hu.kocsisgeri.betterneptun.ui.screen.timetable

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.common.utils.minus
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
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateRange
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
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

    private val events = getEventsUseCase()

    private val currentSelected = MutableStateFlow(defaultSelected)

    private val _viewMode = MutableStateFlow(ViewMode.FULL_WEEK)
    val viewMode = _viewMode.stateWhileSubscribed()

    private val _selectedDate = MutableStateFlow(LocalDateTime.now())
    val selectedDate = _selectedDate.stateWhileSubscribed()

    private val _times = MutableStateFlow("0:23")

    val weeks = combine(
        selectedDate,
        viewMode,
        _times,
        events,
    ) { selectedDate, mode, times, events ->
        listOf(
            createWeekData(
                times = times,
                dateRange = findDateRange(mode, selectedDate.previous(mode)),
                events = events
            ),
            createWeekData(
                times = times,
                dateRange = findDateRange(mode, selectedDate),
                events = events
            ),
            createWeekData(
                times = times,
                dateRange = findDateRange(mode, selectedDate.next(mode)),
                events = events
            )
        )
    }.stateWhileSubscribed(emptyList())

    val selectedEvent = combine(
        currentSelected,
        events
    ) { currentSelectedEventId, events ->
        events.find { event ->
            event.id == currentSelectedEventId
        }
    }.stateWhileSubscribed(null)

    fun selectEvent(eventId: Long?) {
        currentSelected.value = eventId
    }

    fun clearSelectedEvent() {
        currentSelected.tryEmit(null)
    }

    fun addEvent(event: CalendarItem.LocalEvent) {
        viewModelScope.launchReportingErrors {
            addLocalEventsUseCase(event)
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launchReportingErrors {
            deleteLocalEventUseCase(eventId)
            clearSelectedEvent()
        }
    }

    fun next() {
        _selectedDate.update { it.next(viewMode.value) }
    }

    fun previous() {
        _selectedDate.update { it.previous(viewMode.value) }
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun findDateRange(
        mode: ViewMode,
        date: LocalDateTime
    ): LocalDateRange = when (mode) {
        ViewMode.FULL_WEEK -> {
            val monday = date.date.previousOrSame(DayOfWeek.MONDAY)
            val sunday = monday.nextOrSame(DayOfWeek.SUNDAY)

            LocalDateRange(
                start = monday,
                endInclusive = sunday
            )
        }

        ViewMode.WEEK -> {
            val monday = date.date.previousOrSame(DayOfWeek.MONDAY)
            val friday = monday.nextOrSame(DayOfWeek.FRIDAY)

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
            end = LocalTime(maxHour, 0)
        ).apply {
            events.filter { it.startTime.date in dateRange }.forEach {
                add(it.toComposeEvent())
            }
        }
    }

    private fun LocalDateTime.previous(viewMode: ViewMode): LocalDateTime {
        return when(viewMode) {
            ViewMode.WEEK, ViewMode.FULL_WEEK -> minus(7.days)
            ViewMode.DAY -> minus(1.days)
        }
    }

    private fun LocalDateTime.next(viewMode: ViewMode): LocalDateTime {
        return when(viewMode) {
            ViewMode.WEEK, ViewMode.FULL_WEEK -> plus(7.days)
            ViewMode.DAY -> plus(1.days)
        }
    }
}
