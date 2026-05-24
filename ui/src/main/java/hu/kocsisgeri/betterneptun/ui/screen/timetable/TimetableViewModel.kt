package hu.kocsisgeri.betterneptun.ui.screen.timetable

import androidx.lifecycle.viewModelScope
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.usecase.timetable.AddLocalEventUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.timetable.DeleteLocalEventUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.timetable.GetEventsUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.ViewMode
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.toComposeEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@KoinViewModel
class TimetableViewModel(
    @InjectedParam defaultSelected: Long?,
    getEventsUseCase: GetEventsUseCase,
    private val addLocalEventsUseCase: AddLocalEventUseCase,
    private val deleteLocalEventUseCase: DeleteLocalEventUseCase
) : ComposeViewModel() {

    private val events = getEventsUseCase()

    private val currentSelected = MutableStateFlow(defaultSelected)

    private val _viewMode = MutableStateFlow(ViewMode.WEEK)
    val viewMode = _viewMode.stateWhileSubscribed()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.stateWhileSubscribed()

    private val _times = MutableStateFlow("6:23")

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
        date: LocalDate
    ): LocalDateRange = when (mode) {
        ViewMode.FULL_WEEK -> {
            val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val friday = monday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            LocalDateRange(monday, friday)
        }

        ViewMode.WEEK -> {
            val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val friday = monday.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))

            LocalDateRange(monday, friday)
        }

        ViewMode.DAY -> LocalDateRange(date, date)
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
            start = LocalTime.of(minHour, 0),
            end = LocalTime.of(maxHour, 0)
        ).apply {
            events.filter { it.startTime.toLocalDate() in dateRange }.forEach {
                add(it.toComposeEvent())
            }
        }
    }

    private fun LocalDate.previous(viewMode: ViewMode): LocalDate {
        return when(viewMode) {
            ViewMode.WEEK, ViewMode.FULL_WEEK -> minusWeeks(1)
            ViewMode.DAY -> minusDays(1)
        }
    }

    private fun LocalDate.next(viewMode: ViewMode): LocalDate {
        return when(viewMode) {
            ViewMode.WEEK, ViewMode.FULL_WEEK -> plusWeeks(1)
            ViewMode.DAY -> plusDays(1)
        }
    }
}
