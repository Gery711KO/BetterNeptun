package hu.kocsisgeri.betterneptun.data.token

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.domain.token.LogoutRequestListener
import hu.kocsisgeri.betterneptun.domain.token.LogoutRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal class LogoutHandlerImpl(
    private val localDataSource: LocalDataSource
): LogoutRequestListener, LogoutRequester {

    override val onLogoutRequested = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 10
    )

    override fun requestLogout() {
        onLogoutRequested.tryEmit(Unit)
        localDataSource.clearSharedPreferences()

        CoroutineScope(Dispatchers.IO)
            .launch { localDataSource.clearPreferencesDataStore() }
    }
}