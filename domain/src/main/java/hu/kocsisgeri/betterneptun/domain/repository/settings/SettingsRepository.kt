package hu.kocsisgeri.betterneptun.domain.repository.settings

import hu.kocsisgeri.betterneptun.common.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val storedTheme: StateFlow<ThemeMode>
    val notificationDelay: StateFlow<Int>

    fun saveTheme(themeMode: ThemeMode)
    fun saveNotificationDelay(delayMinutes: Int)

    suspend fun purgeLocalData()
}