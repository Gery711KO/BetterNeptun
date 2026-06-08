package hu.kocsisgeri.betterneptun.core.network.di

import hu.kocsisgeri.betterneptun.domain.token.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.Logger
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single
import timber.log.Timber

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
        setupApiClient(ApiConstants.LOCALIZATION_BASE_URL, ktorLogger)
    }

    @Single
    @Named("AuthClient")
    fun provideAuthHttpClient(ktorLogger: Logger) = HttpClient(OkHttp) {
        setupApiClient(ApiConstants.BASE_URL, ktorLogger)
    }

    @Single
    @Named("MainClient")
    fun provideAuthHttpClient(
        @Provided tokenManager: TokenManager,
        ktorLogger: Logger
    ) = HttpClient(OkHttp) {
        setupApiClient(ApiConstants.BASE_URL, ktorLogger)
        installTokenManager(tokenManager)
    }
}
