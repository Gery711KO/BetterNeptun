package hu.kocsisgeri.betterneptun.data.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import hu.kocsisgeri.betterneptun.data.api.APIService
import hu.kocsisgeri.betterneptun.data.api.network.CustomCookieJar
import hu.kocsisgeri.betterneptun.data.api.network.NetworkResponseAdapterFactory
import hu.kocsisgeri.betterneptun.data.api.token.TokenService
import hu.kocsisgeri.betterneptun.data.dao.AppDatabase
import hu.kocsisgeri.betterneptun.data.dao.ColorDao
import hu.kocsisgeri.betterneptun.data.dao.MessageDao
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSourceImpl
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepositoryImpl
import hu.kocsisgeri.betterneptun.data.serialization.Serialization
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

private const val SHARED_DATA = "Better_Neptun_Persistence"
private const val BASE_URL = "https://neptun.uni-obuda.hu/ujhallgato/api/"

val dataModule = module {
    fun provideDataBase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "mainDB")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    fun provideDao(database: AppDatabase): MessageDao {
        return database.savedMessages()
    }

    fun provideColors(database: AppDatabase) : ColorDao {
        return database.savedCourseColors()
    }

    single<AppDatabase> { provideDataBase(application = get()) }
    single { provideDao(database = get()) }
    single { provideColors(database = get()) }
    single<NeptunRepository> {
        NeptunRepositoryImpl(
            networkDataSource =  get(),
            dataManager = get(),
            tokenService = get(),
            navigator = get()
        )
    }

    factory {
        get<Context>().getSharedPreferences(
            SHARED_DATA,
            Context.MODE_PRIVATE
        )
    }

    single {
        TokenService(
            dataManager = get(),
            networkDataSource = get()
        )
    }

    single {
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    single {
        CustomCookieJar()
    }

    factory {
        val loggingInterceptor: HttpLoggingInterceptor = get()
        val jar: CustomCookieJar = get()

        OkHttpClient.Builder()
            .cookieJar(jar)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    factory {
        Retrofit.Builder()
            .client(get()).apply {
                addConverterFactory(
                    Serialization.instance
                        .asConverterFactory("application/json".toMediaType())
                )
                addCallAdapterFactory(NetworkResponseAdapterFactory())
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
