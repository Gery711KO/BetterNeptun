package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.localization.LanguageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton
import retrofit2.http.GET
import retrofit2.http.Path

@Singleton
class LocalizationApiService(
    @Named("LocalizationClient") private val client: HttpClient
) {

    suspend fun getLanguages(): List<LanguageDto> {
        return client.get("_languages").body()
    }

    suspend fun getLocalization(languageKey: String): Map<String, String> {
        return client.get(languageKey).body()
    }
}
