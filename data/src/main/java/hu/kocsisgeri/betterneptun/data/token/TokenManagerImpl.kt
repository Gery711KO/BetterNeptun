package hu.kocsisgeri.betterneptun.data.token

import hu.kocsisgeri.betterneptun.core.network.api.AuthApiService
import hu.kocsisgeri.betterneptun.core.network.model.neptun.AuthenticationRequestDto
import hu.kocsisgeri.betterneptun.data.datasource.LocalCacheKeys
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.domain.token.TokenManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.serializer
import org.koin.core.annotation.Singleton

@Singleton
internal class TokenManagerImpl(
    private val localDataSource: LocalDataSource,
    private val authApiService: AuthApiService
) : TokenManager {

    private val mutex = Mutex()

    override fun getToken() = localDataSource.getFromSharedPreferences<String?>(
        key = TOKEN_KEY,
        defaultValue = null,
        serializer = serializer()
    )

    override fun saveToken(token: String) {
        localDataSource.saveToSharedPreferences(TOKEN_KEY, token, serializer())
    }

    override fun deleteToken() {
        localDataSource.deleteFromSharedPreferences(TOKEN_KEY)
    }

    override suspend fun refreshToken(): String = mutex.withLock {
        val currentUser = localDataSource
            .getFromSharedPreferences<AuthenticationRequestDto?>(
                key = LocalCacheKeys.CURRENT_USER,
                defaultValue = null,
                serializer = serializer()
            )

        checkNotNull(currentUser) { "No user found." }

        authApiService.authenticate(currentUser).run {
            saveToken(data.accessToken)

            data.accessToken
        }
    }

    companion object {
        private const val TOKEN_KEY = "stored_token"
    }
}
