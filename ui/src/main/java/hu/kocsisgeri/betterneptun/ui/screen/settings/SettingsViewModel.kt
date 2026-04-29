package hu.kocsisgeri.betterneptun.ui.screen.settings

import hu.kocsisgeri.betterneptun.common.ThemeMode
import hu.kocsisgeri.betterneptun.common.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.domain.usecase.LogOutUseCase
import hu.kocsisgeri.betterneptun.ui.base.ComposeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val logOutUseCase: LogOutUseCase,
) : ComposeViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.storedTheme
        .stateWhileSubscribed()

    fun saveTheme(theme: ThemeMode) {
        settingsRepository.saveTheme(theme)
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launchReportingErrors {
            logOutUseCase()
        }
    }
}