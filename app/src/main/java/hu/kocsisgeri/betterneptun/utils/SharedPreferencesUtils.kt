package hu.kocsisgeri.betterneptun.utils

import android.content.SharedPreferences
import timber.log.Timber

const val PREF_CURRENT_USER = "CURRENT_SAVED_USER"
const val PREF_STAY_LOGGED_ID = "STAY_LOGGED_IN"
const val PREF_SAVED_THEME = "SAVED_THEME"

const val PREF_FIRST_CLASS_TIME = "FIRST_CLASS_TIME"
const val PREF_LAST_CLASS_TIME = "LAST_CLASS_TIME"
const val PREF_IS_TIMELINE_AUTOMATIC = "IS_TIMELINE_AUTOMATIC"

inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T): T = when (T::class) {
    Boolean::class -> this.getBoolean(key, defaultValue as Boolean) as T
    Float::class -> this.getFloat(key, defaultValue as Float) as T
    Int::class -> this.getInt(key, defaultValue as Int) as T
    Long::class -> this.getLong(key, defaultValue as Long) as T
    String::class -> this.getString(key, defaultValue as String) as T
    else -> {
        Timber.w("Can't get value, unsupported type: ${T::class}")
        defaultValue
    }
}

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    val editor = this.edit()

    when (T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String)
        else -> {
            Timber.w("Can't save value, unsupported type: ${T::class}")
        }
    }
    editor.apply()
}

fun SharedPreferences.delete(key: String) {
    val editor = this.edit()
    editor.remove(key)
    editor.apply()
}