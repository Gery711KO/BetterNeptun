package hu.kocsisgeri.betterneptun.ui.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TimetableViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {
    val eventList = MutableLiveData<List<CalendarEntity.Event>>(listOf())

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
            }.launchIn(viewModelScope)
        }
    }
}