package hu.kocsisgeri.betterneptun.domain.model

import androidx.appcompat.app.AppCompatDelegate

enum class ThemeMode(val mode: Int) {
    AUTO(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    DARK(AppCompatDelegate.MODE_NIGHT_YES),
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO)
}