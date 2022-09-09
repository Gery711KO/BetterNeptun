package hu.kocsisgeri.betterneptun.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import hu.kocsisgeri.betterneptun.utils.PREF_SAVED_THEME
import hu.kocsisgeri.betterneptun.utils.ThemeMode
import hu.kocsisgeri.betterneptun.utils.data_manager.DataManager
import hu.kocsisgeri.betterneptun.utils.getCurrentTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent

class SettingsViewModel(
    private val manager: DataManager,
    context: Context
) : ViewModel(), KoinComponent {

    val context: Context by lazy { context }

    val themeMode: Flow<ThemeMode?> = flow {
        manager.getData(PREF_SAVED_THEME, ThemeMode::class.java).also { theme ->
            saveTheme(theme?: ThemeMode.AUTO)
            emit(theme?: ThemeMode.AUTO)
        }
    }

    fun saveTheme(theme: ThemeMode) {
        manager.putData(PREF_SAVED_THEME, theme)
    }

    fun logout() {
        manager.purgeData()
    }
}