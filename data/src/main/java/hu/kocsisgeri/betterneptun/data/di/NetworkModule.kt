package hu.kocsisgeri.betterneptun.data.di

import hu.kocsisgeri.betterneptun.data.api.AuthApiService
import hu.kocsisgeri.betterneptun.data.api.MainApiService
import hu.kocsisgeri.betterneptun.data.api.token.TokenAuthenticator
import hu.kocsisgeri.betterneptun.data.api.token.TokenInterceptor
import hu.kocsisgeri.betterneptun.common.serialization.Serialization
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.new
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val BASE_URL = "https://neptun.uni-obuda.hu/ujhallgato/api/"

val networkModule = module {
    factory { new(::TokenAuthenticator) }
    factory { new(::TokenInterceptor) }

    factory {
        HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
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