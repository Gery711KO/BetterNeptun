package hu.kocsisgeri.betterneptun.data.token

import hu.kocsisgeri.betterneptun.domain.token.LogoutHandler
import kotlinx.coroutines.flow.MutableSharedFlow

internal class LogoutHandlerImpl: LogoutHandler {

    override val shouldForceLogout = MutableSharedFlow<Boolean>(
        replay = 0,
        extraBufferCapacity = 10
    )

    override fun forceLogout() {
        shouldForceLogout.tryEmit(true)
    }
}