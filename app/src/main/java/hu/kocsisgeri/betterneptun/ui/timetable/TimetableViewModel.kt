package hu.kocsisgeri.betterneptun.ui.timetable

import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.data.repository.course.HomeState
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ViewMode(val days: Int, @DrawableRes val icon : Int) {
    WEEK(5, R.drawable.ic_week_view), DAY(1, R.drawable.ic_day_view)
}

class TimetableViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {
    val eventList = MutableLiveData<List<CalendarEntity.Event>>(listOf())

    val clickHandler = MutableSharedFlow<CalendarEntity.Event>(1, 100)
    val currentSelected = MutableStateFlow<CalendarEntity.Event?>(null)
    val clicked = MutableSharedFlow<CalendarEntity.Event>(1,100)
    val viewMode = MutableStateFlow(ViewMode.WEEK)

    val times = HomeState.firstClassTime.combine(HomeState.lastClassTime) { first, last ->
        "${first?.split(":")?.get(0)?.toInt() ?: 8}:${last?.split(":")?.get(0)?.toInt() ?: 22}"
    }.asLiveData()

    fun addEvents() {
        viewModelScope.launch(Dispatchers.Main) {
            neptunRepository.events.first().let {
                eventList.postValue(it)
            }
        }
    }

    fun changeColor(event: CalendarEntity.Event?, color: Int) {
        viewModelScope.launch {
            neptunRepository.setEventColor(event, color)
            neptunRepository.events.onEach {
                eventList.postValue(it)
            }.launchIn(this)
        }
    }

    init {
        clickHandler.onEach {
            currentSelected.tryEmit(it)
            clicked.tryEmit(it)
        }.launchIn(viewModelScope)
    }
}