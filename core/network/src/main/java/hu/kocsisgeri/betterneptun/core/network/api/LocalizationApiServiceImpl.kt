package hu.kocsisgeri.betterneptun.core.network.api

import hu.kocsisgeri.betterneptun.core.network.model.localization.LanguageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton

@Singleton
internal class LocalizationApiServiceImpl(
    @Named("LocalizationClient") private val client: HttpClient
) : LocalizationApiService {

    override suspend fun getLanguages(): List<LanguageDto> {
        return client.get("_languages").body()
    }

    override suspend fun getLocalization(languageKey: String): Map<String, String> {
        return client.get(languageKey).body()
    }
}
