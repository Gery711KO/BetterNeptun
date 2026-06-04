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
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

private const val SHARED_DATA = "shared_data"
private const val PREFERENCES_DATASTORE_KEY = "preferences_datastore_key"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_DATASTORE_KEY
)

@Module
@Configuration
class DatabaseModule {

    @Single
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE)
    }

    @Single
    internal fun provideDataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Single
    internal fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "mainDB")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Single
    internal fun provideLocalEventsDatabase(appDatabase: AppDatabase): LocalEventsDatabase {
        return LocalEventsDatabaseImpl(localEventDao = appDatabase.localEvents)
    }
}