package hu.kocsisgeri.betterneptun.initialization

import hu.kocsisgeri.betterneptun.domain.auth.LogoutRegistry
import hu.kocsisgeri.betterneptun.domain.auth.LogoutRequester
import hu.kocsisgeri.betterneptun.domain.auth.OnLogoutCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Singleton

@Singleton(createdAtStart = true)
class SessionManager(
    private val scope: CoroutineScope
): LogoutRequester, LogoutRegistry {

    private val mutex = Mutex()
    private val logoutListeners = mutableSetOf<OnLogoutCallback>()

    override fun register(callback: OnLogoutCallback) {
        logoutListeners.add(callback)
    }

    override fun unregister(callback: OnLogoutCallback) {
        logoutListeners.remove(callback)
    }

    override suspend fun requestLogout() {
        if (mutex.isLocked) return

        mutex.withLock {
            scope.launch {
                logoutListeners.map { async { it.onLogout()  } }.awaitAll()
            }
        }
    }
}
