package hu.kocsisgeri.betterneptun.ui.screen.timetable

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ViewMode(val days: Int, @DrawableRes val icon : Int) {
    WEEK(5, R.drawable.ic_week_view), DAY(1, R.drawable.ic_day_view)
}

class TimetableViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {

    private val currentSelected = MutableStateFlow<CalendarEntity.Event?>(null)
    private val eventList = neptunRepository.events

    val timetableEvents = eventList.asLiveData()
//    val times = combine(CourseRepository.firstClassTime, CourseRepository.lastClassTime) { first, last ->
//        "${first?.split(":")?.get(0)?.toInt() ?: 8}:${last?.split(":")?.get(0)?.toInt() ?: 22}"
//    }.asLiveData()

    val clickHandler = MutableSharedFlow<CalendarEntity.Event>(0, 10)
    val clicked = MutableSharedFlow<CalendarEntity.Event>(0,10)
    val viewMode = MutableStateFlow(ViewMode.WEEK)


    fun selectEvent(event: CalendarEntity.Event?) {
        currentSelected.tryEmit(event)
    }

    fun getSelectedEvent(): CalendarEntity.Event? {
        return currentSelected.value
    }

    fun changeColor(event: CalendarEntity.Event?, color: Int) {
        viewModelScope.launch {
            neptunRepository.setEventColor(event, color)
        }
    }

    init {
        clickHandler.onEach {
            currentSelected.tryEmit(it)
            clicked.tryEmit(it)
        }.launchIn(viewModelScope)
    }
}