package hu.kocsisgeri.betterneptun.ui.activity.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow

class SplashViewModel(
    settingsRepository: SettingsRepository
) : ViewModel() {

    /**
     *  wait for initialization during splash screen
     */
    val initialized: LiveData<Boolean> = flow {
        // TODO load stuff here thats needed for the app to start
        emit(true)
    }.asLiveData(Dispatchers.IO)
}