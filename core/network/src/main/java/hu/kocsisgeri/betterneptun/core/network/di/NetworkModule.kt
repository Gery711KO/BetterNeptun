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
import org.koin.core.module.dsl.new
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val BASE_URL = "https://neptun.uni-obuda.hu/ujhallgato/api/"
private const val LOCALIZATION_BASE_URL = "https://cdn.simplelocalize.io/${BuildConfig.LOCALIZATION_TOKEN}/"

val networkModule = module {
    factory { new(::TokenAuthenticator) }
    factory { new(::TokenInterceptor) }

    factory {
        HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
    }

    single(named("LocalizationClient")) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single(named("AuthClient")) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single(named("MainClient")) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<TokenInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .build()
    }

    factory {
        Retrofit.Builder()
            .baseUrl(LOCALIZATION_BASE_URL)
            .client(get(named("LocalizationClient")))
            .addConverterFactory(Serialization.converterFactory)
            .build()
            .create(LocalizationApiService::class.java)
    }

    factory {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get(named("AuthClient")))
            .addConverterFactory(Serialization.converterFactory)
            .build()
            .create(AuthApiService::class.java)
    }

    factory {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get(named("MainClient")))
            .addConverterFactory(Serialization.converterFactory)
            .build()
            .create(MainApiService::class.java)
    }
}