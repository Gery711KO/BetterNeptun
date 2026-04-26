package hu.kocsisgeri.betterneptun.ui.screen.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import hu.kocsisgeri.betterneptun.utils.PREF_SAVED_THEME
import hu.kocsisgeri.betterneptun.utils.ThemeMode
import hu.kocsisgeri.betterneptun.data.datamanager.DataManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent

class SettingsViewModel(
    private val manager: DataManager,
    context: Context
) : ViewModel(), KoinComponent {

    val context: Context by lazy { context }

    val themeMode: Flow<ThemeMode?> = flow {
        manager.getDefault(PREF_SAVED_THEME, ThemeMode.AUTO).also { theme ->
            saveTheme(theme)
            emit(theme)
        }
    }

    fun saveTheme(theme: ThemeMode) {
        manager.putData(PREF_SAVED_THEME, theme)
    }

    fun logout() {
        manager.purgeData()
    }
}