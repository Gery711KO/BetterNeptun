package hu.kocsisgeri.betterneptun.ui.screen.settings

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.domain.usecase.auth.LogoutUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.settings.GetStoredNotificationDelayUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.settings.GetStoredThemeUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.settings.SaveSettingsUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val localizationService: LocalizationService,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val logoutUseCase: LogoutUseCase,
    getStoredNotificationDelayUseCase: GetStoredNotificationDelayUseCase,
    getStoredThemeUseCase: GetStoredThemeUseCase,
) : ComposeViewModel() {

    val languages: StateFlow<List<Language>> = localizationService.languages

    val themeMode: StateFlow<ThemeMode> = getStoredThemeUseCase()
        .stateWhileSubscribed(ThemeMode.AUTO)

    val notificationDelay: StateFlow<Int> = getStoredNotificationDelayUseCase()
        .stateWhileSubscribed(-1)

    fun saveTheme(theme: ThemeMode) {
        viewModelScope.launchReportingErrors {
            saveSettingsUseCase(SaveSettingsUseCase.Setting.Theme(theme))
        }
    }

    fun saveNotificationDelay(delay: Int) {
        viewModelScope.launchReportingErrors {
            saveSettingsUseCase(SaveSettingsUseCase.Setting.NotificationDelay(delay))
        }
    }

    fun changeLanguage(language: Language) {
        viewModelScope.launchReportingErrors {
            localizationService.changeLanguage(language.key)
        }
    }

    fun logout() {
        viewModelScope.launchReportingErrors {
            logoutUseCase()
        }
    }
}
