package hu.kocsisgeri.betterneptun.ui.splash

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import hu.kocsisgeri.betterneptun.utils.PREF_SAVED_THEME
import hu.kocsisgeri.betterneptun.utils.ThemeMode
import hu.kocsisgeri.betterneptun.utils.data_manager.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplashViewModel : ViewModel(), KoinComponent {

    private val dataManager : DataManager by inject()

    /**
     *  wait for initialization during splash screen
     */
    val initialized: LiveData<Boolean> = flow {
        // TODO load stuff here thats needed for the app to start
        emit(true)
    }.asLiveData(Dispatchers.IO)

    init {
        dataManager.getData(PREF_SAVED_THEME, ThemeMode::class.java)?.let {
            AppCompatDelegate.setDefaultNightMode(it.mode)
        }
    }
}