package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.core.network.model.localization.LanguageDto

interface LocalizationDataSource {

    suspend fun getLanguages(): List<LanguageDto>

    suspend fun getLocalization(languageKey: String): Map<String, String>
}
