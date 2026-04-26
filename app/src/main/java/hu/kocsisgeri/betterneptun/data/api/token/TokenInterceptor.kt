package hu.kocsisgeri.betterneptun.data.api.token

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val tokenStore: TokenStore,
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStore.getToken()

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