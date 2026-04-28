package hu.kocsisgeri.betterneptun.data.repository.settings

import androidx.appcompat.app.AppCompatDelegate
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.common.PREF_SAVED_THEME
import hu.kocsisgeri.betterneptun.common.ThemeMode
import hu.kocsisgeri.betterneptun.common.get
import hu.kocsisgeri.betterneptun.common.put
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsRepositoryImpl(
    private val localDataSource: LocalDataSource
): SettingsRepository {

    override val storedTheme = MutableStateFlow(
        localDataSource.cache.get<ThemeMode>(
            key = PREF_SAVED_THEME,
            defaultValue = ThemeMode.AUTO
        )
    )

    init {
        AppCompatDelegate.setDefaultNightMode(
            localDataSource.cache.get<ThemeMode>(
                key = PREF_SAVED_THEME,
                defaultValue = ThemeMode.AUTO
            ).mode
        )
    }

    override fun saveTheme(themeMode: ThemeMode) {
        localDataSource.cache.put(PREF_SAVED_THEME, themeMode)
        storedTheme.value = themeMode
        AppCompatDelegate.setDefaultNightMode(themeMode.mode)
    }

    override suspend fun purgeLocalData() {
        localDataSource.purge()
    }
}