package hu.kocsisgeri.betterneptun.domain.usecase.settings

import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository

class GetStoredNotificationDelayUseCase(private val settingsRepository: SettingsRepository) {

    operator fun invoke() = settingsRepository.notificationDelay
}