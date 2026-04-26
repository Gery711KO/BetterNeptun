package hu.kocsisgeri.betterneptun.ui.activity.splash

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.utils.PREF_SAVED_THEME
import hu.kocsisgeri.betterneptun.utils.PREF_STAY_LOGGED_ID
import hu.kocsisgeri.betterneptun.utils.ThemeMode
import hu.kocsisgeri.betterneptun.utils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SplashViewModel(
    localDataSource: LocalDataSource
) : ViewModel() {

    /**
     *  wait for initialization during splash screen
     */
    val initialized: LiveData<Boolean> = flow {
        // TODO load stuff here thats needed for the app to start
        emit(true)
    }.asLiveData(Dispatchers.IO)

    init {
        localDataSource.cache.get(PREF_SAVED_THEME, ThemeMode.AUTO).let {
            AppCompatDelegate.setDefaultNightMode(it.mode)
        }

        if (!localDataSource.cache.get(PREF_STAY_LOGGED_ID, false)) {
            viewModelScope.launch {
                localDataSource.purge()
            }
        }
    }
}