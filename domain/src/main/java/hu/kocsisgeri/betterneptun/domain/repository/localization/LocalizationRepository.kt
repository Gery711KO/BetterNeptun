package hu.kocsisgeri.betterneptun.domain.repository.localization

import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.model.localization.LocalizationDictionary

interface LocalizationRepository {

    suspend fun getLanguages(): List<Language>
    suspend fun getLocalizationDictionary(language: Language): LocalizationDictionary
}