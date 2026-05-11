package hu.kocsisgeri.betterneptun.domain.repository.settings

import hu.kocsisgeri.betterneptun.common.utils.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val storedTheme: Flow<ThemeMode>
    val notificationDelay: Flow<Int>

    suspend fun saveTheme(themeMode: ThemeMode)
    suspend fun saveNotificationDelay(delayMinutes: Int)

    suspend fun purgeLocalData()
}