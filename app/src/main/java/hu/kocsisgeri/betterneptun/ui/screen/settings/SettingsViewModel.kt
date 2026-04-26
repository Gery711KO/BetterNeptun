package hu.kocsisgeri.betterneptun.ui.screen.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.utils.PREF_SAVED_THEME
import hu.kocsisgeri.betterneptun.utils.ThemeMode
import hu.kocsisgeri.betterneptun.utils.get
import hu.kocsisgeri.betterneptun.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.utils.put
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent

class SettingsViewModel(
    private val localDataSource: LocalDataSource,
    context: Context
) : ViewModel(), KoinComponent {

    val context: Context by lazy { context }

    val themeMode: Flow<ThemeMode?> = flow {
        localDataSource.cache.get(PREF_SAVED_THEME, ThemeMode.AUTO).also { theme ->
            saveTheme(theme)
            emit(theme)
        }
    }

    fun saveTheme(theme: ThemeMode) {
        localDataSource.cache.put(PREF_SAVED_THEME, theme)
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launchReportingErrors {
            localDataSource.purge()
            onLogout()
        }
    }
}