package hu.kocsisgeri.betterneptun.ui.screen.settings

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.ThemeMode
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.domain.usecase.LogOutUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val logOutUseCase: LogOutUseCase,
) : ComposeViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.storedTheme
        .stateWhileSubscribed(ThemeMode.AUTO)

    val notificationDelay: StateFlow<Int> = settingsRepository.notificationDelay
        .stateWhileSubscribed(-1)

    fun saveTheme(theme: ThemeMode) {
        viewModelScope.launchReportingErrors {
            settingsRepository.saveTheme(theme)
        }
    }

    fun saveNotificationDelay(delay: Int) {
        viewModelScope.launchReportingErrors {
            settingsRepository.saveNotificationDelay(delay)
        }
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launchReportingErrors {
            logOutUseCase()
        }
    }
}