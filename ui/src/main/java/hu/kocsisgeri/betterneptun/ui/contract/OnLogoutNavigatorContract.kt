package hu.kocsisgeri.betterneptun.ui.contract

import hu.kocsisgeri.betterneptun.domain.auth.LogoutRegistry
import hu.kocsisgeri.betterneptun.domain.auth.OnLogoutCallback
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton(createdAtStart = true)
class OnLogoutNavigatorContract(
    val navigator: Navigator,
    logoutRegistry: LogoutRegistry
) : OnLogoutCallback {

    init {
        logoutRegistry.register(this)
    }

    override suspend fun onLogout() = withContext(Dispatchers.Main) {
        navigator.navigateToInclusive(LoginDestination)
    }
}
