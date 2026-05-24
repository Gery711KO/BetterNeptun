package hu.kocsisgeri.betterneptun.core.network.interceptors.token

import hu.kocsisgeri.betterneptun.domain.token.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

internal class TokenInterceptor(private val tokenManager: TokenManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()

        return chain.proceed(
            chain.request().newBuilder()
                .let { builder ->
                    token?.let {
                        builder.header("Authorization", "Bearer $token")
                    }?: builder
                }
                .build()
        )
    }
}