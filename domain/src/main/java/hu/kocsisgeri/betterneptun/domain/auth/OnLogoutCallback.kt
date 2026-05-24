package hu.kocsisgeri.betterneptun.domain.auth

/**
 * Interface for observing logout request events.
 */
interface OnLogoutCallback {

    /**
     * Called when a logout event is triggered, allowing implementations
     * to perform necessary cleanup or state resets.
     */
    suspend fun onLogout()
}
