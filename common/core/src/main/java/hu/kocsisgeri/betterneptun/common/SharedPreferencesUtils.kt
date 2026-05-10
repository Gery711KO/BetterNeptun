package hu.kocsisgeri.betterneptun.common

import android.content.SharedPreferences
import androidx.core.content.edit
import hu.kocsisgeri.betterneptun.common.serialization.Serialization
import kotlinx.serialization.KSerializer


const val PREF_CURRENT_USER = "CURRENT_SAVED_USER"
const val PREF_STAY_LOGGED_ID = "STAY_LOGGED_IN"
const val PREF_SAVED_THEME = "SAVED_THEME"
const val PREF_NOTIFICATION_DELAY = "NOTIFICATION_DELAY"


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