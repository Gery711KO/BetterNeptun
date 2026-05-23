package hu.kocsisgeri.betterneptun.ui.screen.settings

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.domain.usecase.LogOutUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val localizationService: LocalizationService,
    private val logOutUseCase: LogOutUseCase,
) : ComposeViewModel() {

    val languages: StateFlow<List<Language>> = localizationService.languages

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

    fun changeLanguage(language: Language) {
        viewModelScope.launchReportingErrors {
            localizationService.changeLanguage(language)
        }
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launchReportingErrors {
            logOutUseCase()
        }
    }
}