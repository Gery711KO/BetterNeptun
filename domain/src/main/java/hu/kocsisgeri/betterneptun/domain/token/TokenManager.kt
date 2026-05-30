package hu.kocsisgeri.betterneptun.domain.token

/**
 * Manager for handling authentication tokens and session persistence.
 */
interface TokenManager {

    /**
     * Retrieves the currently stored authentication token.
     * @return The token string if available, null otherwise.
     */
    fun getToken(): String?

    /**
     * Saves a new authentication token to persistent storage.
     * @param token The token string to save.
     */
    fun saveToken(token: String)

    /**
     * Deletes the currently stored authentication token and clears the session.
     */
    fun deleteToken()

    /**
     * Refreshes the authentication token.
     * @return The newly obtained token string.
     */
    suspend fun refreshToken(): String
}
