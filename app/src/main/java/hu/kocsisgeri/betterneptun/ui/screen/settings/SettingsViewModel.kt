package hu.kocsisgeri.betterneptun.ui.screen.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.utils.PREF_SAVED_THEME
import hu.kocsisgeri.betterneptun.utils.ThemeMode
import hu.kocsisgeri.betterneptun.utils.get
import hu.kocsisgeri.betterneptun.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.utils.put
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

class SettingsViewModel(
    private val localDataSource: LocalDataSource,
    context: Context
) : ViewModel(), KoinComponent {

    val context: Context by lazy { context }

    private val _themeMode = MutableStateFlow<ThemeMode?>(null)
    val themeMode: StateFlow<ThemeMode?> = _themeMode.asStateFlow()

    init {
        _themeMode.value = localDataSource.cache.get(PREF_SAVED_THEME, ThemeMode.AUTO)
    }

    fun saveTheme(theme: ThemeMode) {
        localDataSource.cache.put(PREF_SAVED_THEME, theme)
        _themeMode.value = theme
        AppCompatDelegate.setDefaultNightMode(theme.mode)
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launchReportingErrors {
            localDataSource.purge()
            onLogout()
        }
    }
}