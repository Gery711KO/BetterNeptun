package hu.kocsisgeri.betterneptun.core.network.di

import hu.kocsisgeri.betterneptun.common.utils.serialization.Serialization
import hu.kocsisgeri.betterneptun.core.network.BuildConfig
import hu.kocsisgeri.betterneptun.domain.token.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import timber.log.Timber

private const val BASE_URL = "https://neptun.uni-obuda.hu/ujhallgato/api/"
private const val LOCALIZATION_BASE_URL = "https://cdn.simplelocalize.io/${BuildConfig.LOCALIZATION_TOKEN}/${BuildConfig.LOCALIZATION_SNAPSHOT}/"

@Module
@Configuration
class NetworkModule {

    @Single
    fun provideKtorLogger(): Logger = object : Logger {
        override fun log(message: String) {
            Timber.tag("KtorNetwork").d(message)
        }
    }

    @Single
    @Named("LocalizationClient")
    fun provideLocalizationHttpClient(ktorLogger: Logger) = HttpClient(OkHttp) {
        setupApiClient(LOCALIZATION_BASE_URL, ktorLogger)
    }

    @Single
    @Named("AuthClient")
    fun provideAuthHttpClient(ktorLogger: Logger) = HttpClient(OkHttp) {
        setupApiClient(BASE_URL, ktorLogger)
    }

    @Single
    @Named("MainClient")
    fun provideAuthHttpClient(
        tokenManager: TokenManager,
        ktorLogger: Logger
    ) = HttpClient(OkHttp) {
        setupApiClient(BASE_URL, ktorLogger)
        setupTokenLogic(tokenManager)
    }

    private fun HttpClientConfig<OkHttpConfig>.setupApiClient(
        baseUrl: String,
        ktorLogger: Logger
    ) {
        defaultRequest {
            url(baseUrl)
        }
        install(ContentNegotiation) {
            json(Serialization.instance)
        }
        install(Logging) {
            logger = ktorLogger
            level = LogLevel.BODY
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.setupTokenLogic(tokenManager: TokenManager) {
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
                    BearerTokens(newToken, null)
                }
            }
        }
    }
}
