package hu.kocsisgeri.betterneptun.domain.usecase.auth

import hu.kocsisgeri.betterneptun.domain.auth.LogoutRequester

class LogoutUseCase(private val logoutRequester: LogoutRequester) {

    suspend operator fun invoke() {
        logoutRequester.requestLogout()
    }
}