package hu.kocsisgeri.betterneptun.domain.repository.settings

import hu.kocsisgeri.betterneptun.common.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val storedTheme: StateFlow<ThemeMode>

    fun saveTheme(themeMode: ThemeMode)

    suspend fun purgeLocalData()
}