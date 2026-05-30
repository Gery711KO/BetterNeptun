package hu.kocsisgeri.betterneptun.domain.auth

/**
 * Interface for registering and unregistering logout observers.
 */
interface LogoutRegistry {

    /**
     * Registers a callback to be notified when a logout event occurs.
     */
    fun register(callback: OnLogoutCallback)

    /**
     * Unregisters a previously registered logout callback.
     */
    fun unregister(callback: OnLogoutCallback)
}
