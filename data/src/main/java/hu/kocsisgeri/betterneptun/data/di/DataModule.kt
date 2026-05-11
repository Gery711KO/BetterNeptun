package hu.kocsisgeri.betterneptun.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSourceImpl
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSourceImpl
import hu.kocsisgeri.betterneptun.data.repository.login.LoginRepositoryImpl
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepositoryImpl
import hu.kocsisgeri.betterneptun.data.repository.settings.SettingsRepositoryImpl
import hu.kocsisgeri.betterneptun.data.token.LogoutHandlerImpl
import hu.kocsisgeri.betterneptun.data.token.TokenManagerImpl
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.domain.token.LogoutHandler
import hu.kocsisgeri.betterneptun.domain.token.TokenManager
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.new
import org.koin.dsl.module

private const val SHARED_DATA = "Better_Neptun_Persistence"
private const val PREFERENCES_DATASTORE_KEY = "pref_store_key"

val dataModule = module {
    single { Dispatchers.IO }

    single { get<Context>().sharedPreferences }
    single { get<Context>().dataStore }

    single<NetworkDataSource> { new(::NetworkDataSourceImpl) }
    single<LocalDataSource> { new(::LocalDataSourceImpl) }

    single<NeptunRepository> { new(::NeptunRepositoryImpl) }
    single<LoginRepository> { new(::LoginRepositoryImpl) }
    single<SettingsRepository> { new(::SettingsRepositoryImpl) }

    single<TokenManager> { new(::TokenManagerImpl) }
    single<LogoutHandler> { new(::LogoutHandlerImpl) }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_DATASTORE_KEY)
private val Context.sharedPreferences: SharedPreferences
    get() = getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE)
