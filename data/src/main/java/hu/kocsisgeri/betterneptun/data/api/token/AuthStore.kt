package hu.kocsisgeri.betterneptun.data.api.token

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.common.PREF_CURRENT_USER
import hu.kocsisgeri.betterneptun.common.delete
import hu.kocsisgeri.betterneptun.common.get
import hu.kocsisgeri.betterneptun.common.put
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthStore(
    private val localDataSource: LocalDataSource,
) {

    private val _forceLogout = MutableSharedFlow<Unit>(0, 1)
    val forceLogout =_forceLogout.asSharedFlow()

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

    fun clear() {
        localDataSource.cache.delete(TOKEN_KEY)
        _forceLogout.tryEmit(Unit)
    }

    companion object {
        private const val TOKEN_KEY = "stored_token"
    }
}