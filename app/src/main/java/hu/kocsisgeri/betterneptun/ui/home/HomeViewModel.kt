package hu.kocsisgeri.betterneptun.ui.home

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun fetchMessages(maxPage: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            neptunRepository.fetchMessages(maxPage)
            neptunRepository.fetchCalendarData()
            isLoggedIn.tryEmit(true)
        }
    }
}