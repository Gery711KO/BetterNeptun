package hu.kocsisgeri.betterneptun.domain.token

import kotlinx.coroutines.flow.SharedFlow

interface LogoutRequestListener {

    val onLogoutRequested: SharedFlow<Unit>
}
