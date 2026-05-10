package hu.kocsisgeri.betterneptun.data.repository.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.stringPreferencesKey
import hu.kocsisgeri.betterneptun.common.PREF_NOTIFICATION_DELAY
import hu.kocsisgeri.betterneptun.common.PREF_SAVED_THEME
import hu.kocsisgeri.betterneptun.common.ThemeMode
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer

internal class SettingsRepositoryImpl(
    private val localDataSource: LocalDataSource,
): SettingsRepository {

    override val storedTheme = localDataSource.getFromPreferencesDataStore(
        key = THEME_KEY,
        defaultValue = ThemeMode.AUTO,
        serializer = serializer()
    )

    override val notificationDelay = localDataSource.getFromPreferencesDataStore(
        key = NOTIFICATION_DELAY_KEY,
        defaultValue = 10,
        serializer = serializer()
    )

    init {
        MainScope().launch {
            AppCompatDelegate.setDefaultNightMode(storedTheme.first().mode)
        }
    }

    override suspend fun saveTheme(themeMode: ThemeMode) {
        localDataSource.saveToPreferencesDataStore(
            key = THEME_KEY,
            value = themeMode,
            serializer = serializer()
        )

        AppCompatDelegate.setDefaultNightMode(themeMode.mode)
    }

    override suspend fun saveNotificationDelay(delayMinutes: Int) {
        localDataSource.saveToPreferencesDataStore(
            key = NOTIFICATION_DELAY_KEY,
            value = delayMinutes,
            serializer = serializer()
        )
    }

    override suspend fun purgeLocalData() {
        localDataSource.purge()
    }

    companion object {

        private val THEME_KEY = stringPreferencesKey(PREF_SAVED_THEME)
        private val NOTIFICATION_DELAY_KEY = stringPreferencesKey(PREF_NOTIFICATION_DELAY)
    }
}