package hu.kocsisgeri.betterneptun.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import hu.kocsisgeri.betterneptun.common.serialization.Serialization
import kotlinx.coroutines.flow.map


suspend inline fun <reified T: Any> DataStore<Preferences>.saveData(
    key: Preferences.Key<String>,
    data: T,
) = edit { preferences ->
    preferences[key] = Serialization.instance.encodeToString(data)
}

inline fun <reified T: Any> DataStore<Preferences>.getData(
    key: Preferences.Key<String>,
    defaultValue: T,
) = data.map { preferences ->
    preferences[key]?.let { jsonString ->
        Serialization.instance.decodeFromString<T>(jsonString)
    }?: defaultValue
}