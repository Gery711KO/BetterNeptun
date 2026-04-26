package hu.kocsisgeri.betterneptun.data.datasource

import android.content.SharedPreferences
import hu.kocsisgeri.betterneptun.data.dao.AppDatabase

interface LocalDataSource {

    val appDatabase: AppDatabase
    val cache: SharedPreferences

    suspend fun purge()
}