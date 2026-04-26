package hu.kocsisgeri.betterneptun.data.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import hu.kocsisgeri.betterneptun.data.api.token.TokenStore
import hu.kocsisgeri.betterneptun.data.dao.AppDatabase
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSourceImpl
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSourceImpl
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepositoryImpl
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.new
import org.koin.dsl.module

private const val SHARED_DATA = "Better_Neptun_Persistence"

val dataModule = module {
    factory {
        get<Context>().getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE)
    }

    single { Dispatchers.IO }

    single<AppDatabase> { provideDataBase(application = get()) }

    single<NetworkDataSource> { new(::NetworkDataSourceImpl) }
    single<LocalDataSource> { new(::LocalDataSourceImpl) }

    single<NeptunRepository> { new(::NeptunRepositoryImpl) }

    single { new(::TokenStore) }
}

private fun provideDataBase(application: Application): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, "mainDB")
        .fallbackToDestructiveMigration(false)
        .build()
}
