package hu.kocsisgeri.betterneptun.domain.auth

/**
 * Interface defining a contract for initiating a logout operation.
 */
interface LogoutRequester {

    /**
     * Initiates a request to log out the current user, invalidating existing authentication tokens
     * and terminating the active session.
     */
    suspend fun requestLogout()
}
