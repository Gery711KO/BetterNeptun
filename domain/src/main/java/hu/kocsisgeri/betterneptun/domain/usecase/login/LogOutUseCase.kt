package hu.kocsisgeri.betterneptun.domain.usecase.login

import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase

class LogOutUseCase(
    private val loginRepository: LoginRepository,
    private val neptunRepository: NeptunRepository,
    private val settingsRepository: SettingsRepository,
): UseCase() {

    suspend operator fun invoke() = withLock {
        loginRepository.purge()
        neptunRepository.purge()
        settingsRepository.purgeLocalData()
    }
}
