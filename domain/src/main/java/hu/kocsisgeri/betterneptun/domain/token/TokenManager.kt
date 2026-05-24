package hu.kocsisgeri.betterneptun.domain.token

interface TokenManager {

    fun getToken(): String?
    fun saveToken(token: String)
    fun deleteToken()

    suspend fun refreshToken(): String
}
