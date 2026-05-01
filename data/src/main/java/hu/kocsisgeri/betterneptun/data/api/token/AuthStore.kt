package hu.kocsisgeri.betterneptun.data.api.token

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.common.PREF_CURRENT_USER
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.serializer

internal class AuthStore(
    private val localDataSource: LocalDataSource,
) {

    private val _forceLogout = MutableSharedFlow<Unit>(0, 1)
    val forceLogout =_forceLogout.asSharedFlow()

    fun saveToken(token: String): String {
        localDataSource.saveToSharedPreferences(TOKEN_KEY, token, serializer())

        return token
    }

    fun getToken() = localDataSource.getFromSharedPreferences<String?>(
        key = TOKEN_KEY,
        defaultValue = null,
        serializer = serializer()
    )

    fun getUser() =
        localDataSource.getFromSharedPreferences<AuthenticationRequestDto?>(
            key = PREF_CURRENT_USER,
            defaultValue = null,
            serializer = serializer()
        )

    fun clear() {
        localDataSource.clearSharedPreferences()

        _forceLogout.tryEmit(Unit)
    }

    companion object {
        private const val TOKEN_KEY = "stored_token"
    }
}