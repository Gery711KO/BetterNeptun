package hu.kocsisgeri.betterneptun.domain.usecase.settings

import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository

class SaveSettingsUseCase(private val settingsRepository: SettingsRepository) {

    suspend operator fun invoke(setting: Setting) {
        when (setting) {
            is Setting.NotificationDelay -> settingsRepository.saveNotificationDelay(setting.delay)
            is Setting.Theme -> settingsRepository.saveTheme(setting.themeMode)
        }
    }

    sealed interface Setting {

        data class Theme(val themeMode: ThemeMode): Setting
        data class NotificationDelay(val delay: Int): Setting
    }
}