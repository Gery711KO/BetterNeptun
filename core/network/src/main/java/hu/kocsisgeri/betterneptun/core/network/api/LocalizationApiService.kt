package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.localization.LanguageDto

interface LocalizationApiService {

    suspend fun getLanguages(): List<LanguageDto>
    suspend fun getLocalization(languageKey: String): Map<String, String>
}
