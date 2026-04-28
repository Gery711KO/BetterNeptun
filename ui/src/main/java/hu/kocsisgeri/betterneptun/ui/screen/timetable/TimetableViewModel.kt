package hu.kocsisgeri.betterneptun.ui.screen.timetable

import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.domain.model.CalendarEntity
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.common.launchReportingErrors
import kotlinx.coroutines.flow.*

enum class ViewMode(val days: Int, @DrawableRes val icon : Int) {
    WEEK(5, R.drawable.ic_week_view), DAY(1, R.drawable.ic_day_view)
}

class TimetableViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {

    private val currentSelected = MutableStateFlow<CalendarEntity.Event?>(null)
    private val eventList = neptunRepository.events

    val timetableEvents = eventList.asLiveData()
    val times = MutableLiveData("8:22") // todo get correct values

    val clickHandler = MutableSharedFlow<CalendarEntity.Event>(0, 10)
    val viewMode = MutableStateFlow(ViewMode.WEEK)

    fun getSelectedEvent() = currentSelected.asStateFlow()

    fun changeColor(event: CalendarEntity.Event?, color: Int) {
        viewModelScope.launchReportingErrors {
            neptunRepository.setEventColor(event, color)
        }
    }

    fun clearSelectedEvent() {
        currentSelected.tryEmit(null)
    }

    init {
        clickHandler.onEach {
            currentSelected.tryEmit(it)
        }.launchIn(viewModelScope)
    }
}