package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.localization.LanguageDto
import retrofit2.http.GET
import retrofit2.http.Path

interface LocalizationApiService {

    @GET("_languages")
    suspend fun getLanguages(): List<LanguageDto>

    @GET("{languageKey}")
    suspend fun getLocalization(@Path("languageKey") languageKey: String): Map<String, String>
}
