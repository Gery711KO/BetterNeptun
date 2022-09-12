package hu.kocsisgeri.betterneptun.ui.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class TimetableViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {
    val eventList = MutableLiveData<List<CalendarEntity.Event>>(listOf())

    val clickHandler = MutableSharedFlow<CalendarEntity.Event>(1, 100)
    val currentSelected = MutableStateFlow<CalendarEntity.Event?>(null)
    val clicked = MutableSharedFlow<Int>(1,100)

    fun addEvents() {
        viewModelScope.launch(Dispatchers.Main) {
            neptunRepository.events.first().let {
                eventList.postValue(it)
            }
        }
    }

    fun randomizeColors() {
        viewModelScope.launch {
            neptunRepository.randomiseCalendarColors()
            neptunRepository.events.onEach {
                eventList.postValue(it)
            }.launchIn(this)
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
            clicked.tryEmit(it.color)
        }.launchIn(viewModelScope)
    }
}