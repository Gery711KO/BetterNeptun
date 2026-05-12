package hu.kocsisgeri.betterneptun.core.database.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import hu.kocsisgeri.betterneptun.core.database.localevents.LocalEventsDatabase
import hu.kocsisgeri.betterneptun.core.database.localevents.LocalEventsDatabaseImpl
import hu.kocsisgeri.betterneptun.core.database.room.AppDatabase
import org.koin.dsl.module

private const val SHARED_DATA = "Better_Neptun_Persistence"
private const val PREFERENCES_DATASTORE_KEY = "pref_store_key"

val databaseModule = module {
    single { get<Context>().sharedPreferences }
    single { get<Context>().dataStore }

    single<AppDatabase> {
        provideDataBase(get())
    }

    single<LocalEventsDatabase> {
        LocalEventsDatabaseImpl(localEventDao = get<AppDatabase>().localEvents)
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_DATASTORE_KEY
)

private val Context.sharedPreferences: SharedPreferences
    get() = getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE)


private fun provideDataBase(application: Application): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, "mainDB")
        .fallbackToDestructiveMigration(false)
        .build()
}