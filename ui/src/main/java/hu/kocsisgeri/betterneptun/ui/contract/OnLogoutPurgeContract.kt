package hu.kocsisgeri.betterneptun.ui.contract

import hu.kocsisgeri.betterneptun.domain.auth.LogoutRegistry
import hu.kocsisgeri.betterneptun.domain.auth.OnLogoutCallback
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import org.koin.core.annotation.Singleton

@Singleton(createdAtStart = true)
class OnLogoutPurgeContract(
    private val loginRepository: LoginRepository,
    logoutRegistry: LogoutRegistry
): OnLogoutCallback {

    init {
        logoutRegistry.register(this)
    }

    override suspend fun onLogout() {
        loginRepository.purge()
    }
}
