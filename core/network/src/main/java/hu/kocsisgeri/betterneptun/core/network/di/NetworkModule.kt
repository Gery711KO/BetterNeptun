package hu.kocsisgeri.betterneptun.core.network.di

import hu.kocsisgeri.betterneptun.common.utils.serialization.Serialization
import hu.kocsisgeri.betterneptun.core.network.BuildConfig
import hu.kocsisgeri.betterneptun.core.network.api.AuthApiService
import hu.kocsisgeri.betterneptun.core.network.api.LocalizationApiService
import hu.kocsisgeri.betterneptun.core.network.api.MainApiService
import hu.kocsisgeri.betterneptun.core.network.interceptors.token.TokenAuthenticator
import hu.kocsisgeri.betterneptun.core.network.interceptors.token.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.Retrofit

private const val BASE_URL = "https://neptun.uni-obuda.hu/ujhallgato/api/"
private const val LOCALIZATION_BASE_URL = "https://cdn.simplelocalize.io/${BuildConfig.LOCALIZATION_TOKEN}/${BuildConfig.LOCALIZATION_SNAPSHOT}/"

@Module
@Configuration
class NetworkModule {

    @Factory
    internal fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
    }

    @Single
    @Named("LocalizationClient")
    internal fun provideLocalizationClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Single
    @Named("AuthClient")
    internal fun provideAuthClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Single
    @Named("MainClient")
    internal fun provideMainClient(
        loggingInterceptor: HttpLoggingInterceptor,
        tokenInterceptor: TokenInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Factory
    internal fun provideLocalizationApiService(
        @Named("LocalizationClient") client: OkHttpClient
    ): LocalizationApiService {
        return Retrofit.Builder()
            .baseUrl(LOCALIZATION_BASE_URL)
            .client(client)
            .addConverterFactory(Serialization.converterFactory)
            .build()
            .create(LocalizationApiService::class.java)
    }

    @Factory
    internal fun provideAuthApiService(
        @Named("AuthClient") client: OkHttpClient
    ): AuthApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(Serialization.converterFactory)
            .build()
            .create(AuthApiService::class.java)
    }

    @Factory
    internal fun provideMainApiService(
        @Named("MainClient") client: OkHttpClient
    ): MainApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(Serialization.converterFactory)
            .build()
            .create(MainApiService::class.java)
    }
}
