package hu.kocsisgeri.betterneptun.domain.usecase.login

import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository

class LogOutUseCase(
    private val loginRepository: LoginRepository,
    private val neptunRepository: NeptunRepository,
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke() {
        loginRepository.purge()
        neptunRepository.purge()
        settingsRepository.purgeLocalData()
    }
}