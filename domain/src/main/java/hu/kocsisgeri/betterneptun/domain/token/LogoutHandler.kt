package hu.kocsisgeri.betterneptun.domain.token

import kotlinx.coroutines.flow.SharedFlow

interface LogoutHandler {

    val shouldForceLogout: SharedFlow<Boolean>

    fun forceLogout()
}