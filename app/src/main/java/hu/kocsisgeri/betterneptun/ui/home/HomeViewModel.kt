package hu.kocsisgeri.betterneptun.ui.home

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.utils.PREF_SAVED_THEME
import hu.kocsisgeri.betterneptun.utils.ThemeMode
import hu.kocsisgeri.betterneptun.utils.data_manager.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val neptunRepository: NeptunRepository
) : ViewModel() {

    val isLoggedIn = MutableStateFlow(false)

    fun fetchMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            CourseRepo.fetchCalendarTimes()
            neptunRepository.fetchMessages()
            if (!isLoggedIn.value) neptunRepository.fetchCalendarData()
            isLoggedIn.tryEmit(true)
        }
    }
}