package hu.kocsisgeri.betterneptun.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import hu.kocsisgeri.betterneptun.data.api.AuthApiService
import hu.kocsisgeri.betterneptun.data.api.MainApiService
import hu.kocsisgeri.betterneptun.data.api.network.CustomCookieJar
import hu.kocsisgeri.betterneptun.data.api.token.TokenAuthenticator
import hu.kocsisgeri.betterneptun.data.api.token.TokenInterceptor
import hu.kocsisgeri.betterneptun.data.serialization.Serialization
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.new
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit


private const val BASE_URL = "https://neptun.uni-obuda.hu/ujhallgato/api/"

val networkModule = module {
    single { CustomCookieJar() }

    factory { new(::TokenAuthenticator) }
    factory { new(::TokenInterceptor) }


    single {
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    single(named("AuthClient")) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single(named("MainClient")) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<TokenInterceptor>()) // Ez adja hozzá az alap tokent
            .authenticator(get<TokenAuthenticator>()) // Ez kezeli a 401-et
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