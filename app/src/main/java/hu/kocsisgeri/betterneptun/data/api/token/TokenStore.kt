package hu.kocsisgeri.betterneptun.data.api.token

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.utils.PREF_CURRENT_USER
import hu.kocsisgeri.betterneptun.utils.get
import hu.kocsisgeri.betterneptun.utils.put

class TokenStore(
    private val localDataSource: LocalDataSource,
) {
    fun saveToken(token: String): String {
        localDataSource.cache.put(TOKEN_KEY, token)

        return token
    }

    fun getToken() = localDataSource.cache.get<String?>(
        key = TOKEN_KEY,
        defaultValue = null
    )

    fun getUser() =
        localDataSource.cache.get<AuthenticationRequestDto?>(
            key = PREF_CURRENT_USER,
            defaultValue = null,
        )

    companion object {
        private const val TOKEN_KEY = "stored_token"
    }
}