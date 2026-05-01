package hu.kocsisgeri.betterneptun.data.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import hu.kocsisgeri.betterneptun.common.delete
import hu.kocsisgeri.betterneptun.common.get
import hu.kocsisgeri.betterneptun.common.put
import hu.kocsisgeri.betterneptun.common.serialization.Serialization
import hu.kocsisgeri.betterneptun.data.dao.AppDatabase
import hu.kocsisgeri.betterneptun.data.dao.LocalEventDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer

internal class LocalDataSourceImpl(
    roomDataBase: AppDatabase,
    private val cache: SharedPreferences,
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher,
) : LocalDataSource {

    override val localEvents: LocalEventDao = roomDataBase.localEvents

    override fun <T> saveToSharedPreferences(
        key: String,
        value: T,
        serializer: KSerializer<T>
    ) {
        cache.put(
            key = key,
            value = value,
            serializer = serializer
        )
    }

    override fun <T> getFromSharedPreferences(
        key: String,
        defaultValue: T,
        serializer: KSerializer<T>
    ): T = cache.get(
        key = key,
        defaultValue = defaultValue,
        serializer = serializer
    )

    override fun deleteFromSharedPreferences(key: String) {
        cache.delete(key)
    }

    override fun clearSharedPreferences() {
        cache.edit { clear() }
    }

    override fun <T> getFromPreferencesDataStore(
        key: Preferences.Key<String>,
        defaultValue: T,
        serializer: KSerializer<T>
    ): Flow<T> = dataStore.data.map { preferences ->
        val cached = preferences[key]
        cached?.let {
            Serialization.instance.decodeFromString(serializer, cached)
        } ?: defaultValue
    }

    override suspend fun <T> saveToPreferencesDataStore(
        key: Preferences.Key<String>,
        value: T,
        serializer: KSerializer<T>
    ) {
        dataStore.edit { preferences ->
            preferences[key] = Serialization.instance.encodeToString(serializer, value)
        }
    }

    override suspend fun deleteFromPreferencesDataStore(key: Preferences.Key<String>) {
        dataStore.edit { it.remove(key) }
    }

    override suspend fun clearPreferencesDataStore() {
        dataStore.edit { it.clear() }
    }

    override suspend fun purge() {
        withContext(ioDispatcher) {
            cache.edit { clear() }
            dataStore.edit { it.clear() }
            localEvents.deleteAll()
        }
    }
}