package hu.kocsisgeri.betterneptun.ui.screen.timetable

import androidx.annotation.DrawableRes
import androidx.lifecycle.viewModelScope
import de.tobiasschuerg.weekview.data.LocalDateRange
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.CalendarItem
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class ViewMode(val days: Int, @DrawableRes val icon : Int) {
    WEEK(5, R.drawable.ic_week_view), DAY(1, R.drawable.ic_day_view)
}

class TimetableViewModel(
    private val neptunRepository: NeptunRepository,
) : ComposeViewModel() {

    private val currentSelected = MutableStateFlow<Long?>(null)

    private val _viewMode = MutableStateFlow(ViewMode.WEEK)
    val viewMode = _viewMode.stateWhileSubscribed()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.stateWhileSubscribed()

    private val _times = MutableStateFlow("6:23")
    val times = _times.stateWhileSubscribed()

    val dateRange = combine(selectedDate, viewMode) { date, mode ->
        when (mode) {
            ViewMode.WEEK -> {
                val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val friday = monday.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
                LocalDateRange(monday, friday)
            }
            ViewMode.DAY -> LocalDateRange(date, date)
        }
    }.stateWhileSubscribed(LocalDateRange(LocalDate.now(), LocalDate.now()))

    val timetableEvents = combine(neptunRepository.events, dateRange) { events, range ->
        events.filter { it.startTime.toLocalDate() in range }
    }.stateWhileSubscribed(emptyList())

    val selectedEvent = combine(
        currentSelected,
        neptunRepository.events
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
            neptunRepository.addLocalEvent(event)
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launchReportingErrors {
            neptunRepository.deleteLocalEvent(eventId)
            clearSelectedEvent()
        }
    }

    fun next() {
        _selectedDate.update {
            when (viewMode.value) {
                ViewMode.WEEK -> it.plusWeeks(1)
                ViewMode.DAY -> it.plusDays(1)
            }
        }
    }

    fun previous() {
        _selectedDate.update {
            when (viewMode.value) {
                ViewMode.WEEK -> it.minusWeeks(1)
                ViewMode.DAY -> it.minusDays(1)
            }
        }
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }
}