package hu.kocsisgeri.betterneptun.core.network.interceptors.token

import hu.kocsisgeri.betterneptun.domain.token.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

internal class TokenAuthenticator(private val tokenManager: TokenManager): Authenticator {

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
            val currentToken = tokenManager.getToken()
            val requestHeader = response.request.header("Authorization")

            if (currentToken != null && requestHeader != "Bearer $currentToken") {
                return@runBlocking response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            try {
                val newToken = tokenManager.refreshToken()
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } catch (e: Exception) {
                tokenManager.deleteToken()
                null
            }
        }
    }
}