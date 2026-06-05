package hu.kocsisgeri.betterneptun.ui.contract

import hu.kocsisgeri.betterneptun.domain.clearable.Clearable
import hu.kocsisgeri.betterneptun.domain.auth.LogoutRegistry
import hu.kocsisgeri.betterneptun.domain.auth.OnLogoutCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton(createdAtStart = true)
class OnLogoutPurgeContract(
    private val clearables: List<Clearable>,
    logoutRegistry: LogoutRegistry
) : OnLogoutCallback {

    init {
        logoutRegistry.register(this)
    }

    override suspend fun onLogout() {
        withContext(Dispatchers.IO) {
            clearables.map {
                async { it.clear(this) }
            }.awaitAll()
        }
    }
}
