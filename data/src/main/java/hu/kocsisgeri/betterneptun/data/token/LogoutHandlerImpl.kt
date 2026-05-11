package hu.kocsisgeri.betterneptun.data.token

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.domain.token.LogoutHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal class LogoutHandlerImpl(private val localDataSource: LocalDataSource): LogoutHandler {

    override val shouldForceLogout = MutableSharedFlow<Boolean>(
        replay = 0,
        extraBufferCapacity = 10
    )

    override fun forceLogout() {
        shouldForceLogout.tryEmit(true)
        localDataSource.clearSharedPreferences()

        CoroutineScope(Dispatchers.IO)
            .launch { localDataSource.clearPreferencesDataStore() }
    }
}