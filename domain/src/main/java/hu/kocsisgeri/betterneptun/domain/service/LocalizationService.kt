package hu.kocsisgeri.betterneptun.domain.service

import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import kotlinx.coroutines.flow.StateFlow

interface LocalizationService {

    val languages: StateFlow<List<Language>>

    suspend fun changeLanguage(languageKey: String)

    fun localized(key: Localization, vararg args: String): String
}
