package hu.kocsisgeri.betterneptun.domain.repository.settings

import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val storedLanguage: Flow<String>
    val storedTheme: Flow<ThemeMode>
    val notificationDelay: Flow<Int>

    suspend fun saveTheme(themeMode: ThemeMode)
    suspend fun saveNotificationDelay(delayMinutes: Int)
    suspend fun saveLanguage(languageKey: String)

    suspend fun purgeLocalData()
}