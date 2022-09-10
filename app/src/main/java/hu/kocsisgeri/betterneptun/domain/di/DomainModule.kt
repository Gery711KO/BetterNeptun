package hu.kocsisgeri.betterneptun.domain.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import hu.kocsisgeri.betterneptun.data.dao.AppDatabase
import hu.kocsisgeri.betterneptun.data.dao.MessageDao
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepositoryImpl
import hu.kocsisgeri.betterneptun.domain.api.APIService
import hu.kocsisgeri.betterneptun.domain.api.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.domain.api.datasource.NetworkDataSourceImpl
import hu.kocsisgeri.betterneptun.domain.api.network.CustomCookieJar
import hu.kocsisgeri.betterneptun.domain.api.network.NetworkResponseAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*


private const val SHARED_DATA = "Better_Neptun_Persistence"
private const val BASE_URL = "https://neptun.uni-obuda.hu/hallgato/"

val domainModule = module {
    factory<Moshi> {
        Moshi.Builder().apply {
            add(Date::class.java, Rfc3339DateJsonAdapter())
            add(KotlinJsonAdapterFactory())
        }.build()
    }

    factory {
        get<Context>().getSharedPreferences(
            SHARED_DATA,
            Context.MODE_PRIVATE
        )
    }

    single {
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    factory {
        val converter = MoshiConverterFactory.create(get())
        converter.withNullSerialization()
    }

    single {
        CustomCookieJar()
    }

    factory {
        val loggingInterceptor: HttpLoggingInterceptor = get()
        val jar : CustomCookieJar = get()
        //val cookieJar: CookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(get<Context>()))

        OkHttpClient.Builder()
            .cookieJar(jar)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    factory {
        Retrofit.Builder()
            .client(get()).apply {
                addConverterFactory(get<MoshiConverterFactory>())
                addCallAdapterFactory(NetworkResponseAdapterFactory())
                addConverterFactory(GsonConverterFactory.create())
            }
            .baseUrl(BASE_URL)
            .build()
    }

    factory {
        val retrofit = get<Retrofit>()
        retrofit.create(APIService::class.java)
    }

    single<NetworkDataSource> {
        NetworkDataSourceImpl(api = get(), cookieJar = get())
    }
}

val dataModule = module {
    fun provideDataBase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "mainDB")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideDao(dataBase: AppDatabase): MessageDao {
        return dataBase.messageDao()
    }

    single { provideDataBase(application = get()) }
    single { provideDao(dataBase = get()) }
    single<NeptunRepository> {
        NeptunRepositoryImpl(
            networkDataSource =  get(),
            dao = get(),
            dataManager = get()
        )
    }
}
