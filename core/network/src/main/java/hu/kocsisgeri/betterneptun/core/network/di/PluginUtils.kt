package hu.kocsisgeri.betterneptun.core.network.di

import hu.kocsisgeri.betterneptun.common.utils.serialization.Serialization
import hu.kocsisgeri.betterneptun.domain.token.TokenManager
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration.Companion.seconds

internal fun HttpClientConfig<OkHttpConfig>.installTokenManager(tokenManager: TokenManager) {
    install(Auth) {
        bearer {
            tokenManager.getToken()?.let { token ->
                loadTokens {
                    BearerTokens(
                        accessToken = token,
                        refreshToken = null
                    )
                }
            }

            refreshTokens {
                val newToken = tokenManager.refreshToken()
                newToken?.let {
                    BearerTokens(newToken, null)
                }
            }
        }
    }
}

internal fun HttpClientConfig<OkHttpConfig>.setupApiClient(
    baseUrl: String,
    ktorLogger: Logger
) {
    defaultRequest {
        url(baseUrl)
    }

    install(ContentNegotiation) {
        json(Serialization.instance)
    }

    install(HttpRequestRetry) {
        retryIf(maxRetries = 3) { _, response ->
            response.status.value in 500..599
        }
        retryOnExceptionIf(maxRetries = 2) { _, cause ->
            cause is HttpRequestTimeoutException
        }
        delayMillis { retry ->
            retry * 1.seconds.inWholeMilliseconds
        }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 15.seconds.inWholeMilliseconds
        connectTimeoutMillis = 5.seconds.inWholeMilliseconds
        socketTimeoutMillis = 5.seconds.inWholeMilliseconds
    }

    install(Logging) {
        logger = ktorLogger
        level = LogLevel.BODY
    }
}
