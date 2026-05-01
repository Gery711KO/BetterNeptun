package hu.kocsisgeri.betterneptun.data.datasource

import androidx.datastore.preferences.core.Preferences
import hu.kocsisgeri.betterneptun.data.dao.LocalEventDao
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

internal interface LocalDataSource {

    val localEvents: LocalEventDao

    fun <T> saveToSharedPreferences(
        key: String,
        value: T,
        serializer: KSerializer<T>
    )

    fun <T> getFromSharedPreferences(
        key: String,
        defaultValue: T,
        serializer: KSerializer<T>
    ): T

    fun deleteFromSharedPreferences(key: String)
    fun clearSharedPreferences()

    fun <T> getFromPreferencesDataStore(
        key: Preferences.Key<String>,
        defaultValue: T,
        serializer: KSerializer<T>
    ): Flow<T>

    suspend fun <T> saveToPreferencesDataStore(
        key: Preferences.Key<String>,
        value: T,
        serializer: KSerializer<T>
    )

    suspend fun deleteFromPreferencesDataStore(key: Preferences.Key<String>)
    suspend fun clearPreferencesDataStore()

    suspend fun purge()
}