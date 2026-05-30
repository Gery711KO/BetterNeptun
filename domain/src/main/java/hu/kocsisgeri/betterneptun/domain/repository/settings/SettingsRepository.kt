package hu.kocsisgeri.betterneptun.domain.repository.settings

import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing application settings and user preferences.
 */
interface SettingsRepository {

    /**
     * A [Flow] of the currently selected language key (e.g., "en", "hu").
     */
    val storedLanguage: Flow<String>

    /**
     * A [Flow] of the currently selected [ThemeMode] (e.g., Light, Dark, System).
     */
    val storedTheme: Flow<ThemeMode>

    /**
     * A [Flow] of the notification delay in minutes.
     */
    val notificationDelay: Flow<Int>

    /**
     * Saves the selected theme mode to persistent storage.
     * @param themeMode The [ThemeMode] to save.
     */
    suspend fun saveTheme(themeMode: ThemeMode)

    /**
     * Saves the notification delay to persistent storage.
     * @param delayMinutes The delay in minutes.
     */
    suspend fun saveNotificationDelay(delayMinutes: Int)

    /**
     * Saves the selected language key to persistent storage.
     * @param languageKey The language key to save.
     */
    suspend fun saveLanguage(languageKey: String)
}
