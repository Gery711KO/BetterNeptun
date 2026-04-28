package hu.kocsisgeri.betterneptun.ui.screen.settings

import androidx.lifecycle.ViewModel
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.domain.usecase.LogOutUseCase
import hu.kocsisgeri.betterneptun.common.ThemeMode
import hu.kocsisgeri.betterneptun.common.launchReportingErrors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val logOutUseCase: LogOutUseCase,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.storedTheme

    fun saveTheme(theme: ThemeMode) {
        settingsRepository.saveTheme(theme)
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launchReportingErrors {
            logOutUseCase()
        }
    }
}