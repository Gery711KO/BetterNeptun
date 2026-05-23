package hu.kocsisgeri.betterneptun.domain.service

import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import kotlinx.coroutines.flow.StateFlow

interface LocalizationService: Initializable {

    val languages: StateFlow<List<Language>>

    suspend fun changeLanguage(language: Language)

    fun localized(id: Int, vararg args: String): String
    fun localized(key: String, vararg args: String): String
}