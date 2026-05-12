package hu.kocsisgeri.betterneptun.data.repository.settings

import androidx.datastore.preferences.core.stringPreferencesKey
import hu.kocsisgeri.betterneptun.data.datasource.LocalCacheKeys
import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
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

    override suspend fun saveTheme(themeMode: ThemeMode) {
        localDataSource.saveToPreferencesDataStore(
            key = THEME_KEY,
            value = themeMode,
            serializer = serializer()
        )
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

        private val THEME_KEY =
            stringPreferencesKey(LocalCacheKeys.SAVED_THEME)

        private val NOTIFICATION_DELAY_KEY =
            stringPreferencesKey(LocalCacheKeys.NOTIFICATION_DELAY)
    }
}