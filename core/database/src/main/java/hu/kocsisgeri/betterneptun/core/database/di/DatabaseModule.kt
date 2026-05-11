package hu.kocsisgeri.betterneptun.core.database.di

import android.app.Application
import androidx.room.Room
import hu.kocsisgeri.betterneptun.core.database.localevents.LocalEventsDatabase
import hu.kocsisgeri.betterneptun.core.database.localevents.LocalEventsDatabaseImpl
import hu.kocsisgeri.betterneptun.core.database.room.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<LocalEventsDatabase> {
        LocalEventsDatabaseImpl(
            localEventDao = provideDataBase(application = get()).localEvents
        )
    }
}

private fun provideDataBase(application: Application): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, "mainDB")
        .fallbackToDestructiveMigration(false)
        .build()
}