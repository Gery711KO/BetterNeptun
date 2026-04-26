package hu.kocsisgeri.betterneptun.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import hu.kocsisgeri.betterneptun.data.serialization.Serialization

const val PREF_CURRENT_USER = "CURRENT_SAVED_USER"
const val PREF_STAY_LOGGED_ID = "STAY_LOGGED_IN"
const val PREF_SAVED_THEME = "SAVED_THEME"

const val PREF_FIRST_CLASS_TIME = "FIRST_CLASS_TIME"
const val PREF_LAST_CLASS_TIME = "LAST_CLASS_TIME"
const val PREF_IS_TIMELINE_AUTOMATIC = "IS_TIMELINE_AUTOMATIC"

inline fun <reified T> SharedPreferences.get(
    key: String,
    defaultValue: T,
): T {
    return getString(
        key, Serialization.instance.encodeToString(defaultValue)
    )?.let {
        Serialization.instance.decodeFromString(it)
    }?: defaultValue
}

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    this.edit {
        putString(key, Serialization.instance.encodeToString(value))
    }
}

fun SharedPreferences.delete(key: String) {
    this.edit {
        remove(key)
    }
}