package hu.kocsisgeri.betterneptun.data.util

import android.content.SharedPreferences
import androidx.core.content.edit
import hu.kocsisgeri.betterneptun.common.utils.serialization.Serialization
import kotlinx.serialization.KSerializer

inline fun <reified T> SharedPreferences.get(
    key: String,
    defaultValue: T,
): T {
    return getString(
        key, null
    )?.let {
        Serialization.instance.decodeFromString<T>(it)
    }?: defaultValue
}

fun <T> SharedPreferences.get(
    key: String,
    defaultValue: T,
    serializer: KSerializer<T>
): T {
    return getString(
        key, null
    )?.let {
        Serialization.instance.decodeFromString(serializer, it)
    }?: defaultValue
}

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    this.edit {
        putString(key, Serialization.instance.encodeToString(value))
    }
}

fun <T> SharedPreferences.put(key: String, value: T, serializer: KSerializer<T>) {
    this.edit {
        putString(key, Serialization.instance.encodeToString(serializer, value))
    }
}

fun SharedPreferences.delete(key: String) {
    this.edit {
        remove(key)
    }
}