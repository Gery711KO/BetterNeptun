package hu.kocsisgeri.betterneptun.domain.usecase.settings

import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import org.koin.core.annotation.Factory

@Factory
class GetStoredThemeUseCase(private val settingsRepository: SettingsRepository) {

    operator fun invoke() = settingsRepository.storedTheme
}
