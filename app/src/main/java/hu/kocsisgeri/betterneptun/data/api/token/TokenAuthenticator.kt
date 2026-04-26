package hu.kocsisgeri.betterneptun.data.api.token

import hu.kocsisgeri.betterneptun.data.api.AuthApiService
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val authApiService: AuthApiService,
    private val navigator: Navigator,
): Authenticator {

    private val mutex = Mutex()
    private val Response.responseCount: Int
        get() {
            var result = 1
            var lastResponse = priorResponse
            while (lastResponse != null) {
                result++
                lastResponse = lastResponse.priorResponse
            }
            return result
        }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.responseCount >= 3) return null

        return runBlocking {
            val currentToken = tokenStore.getToken()
            val requestHeader = response.request.header("Authorization")

            if (currentToken != null && requestHeader != "Bearer $currentToken") {
                return@runBlocking response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            try {
                val newToken = refreshToken() // mutex-szel védett
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } catch (e: Exception) {
                navigator.navigateToInclusive(LoginDestination)
                null
            }
        }
    }

    private suspend fun refreshToken(): String = mutex.withLock {
        val currentUser = tokenStore.getUser()

        checkNotNull(currentUser) { "No user found." }

        authApiService.authenticate(currentUser).let {
            tokenStore.saveToken(it.data.accessToken)
        }
    }
}