package hu.kocsisgeri.betterneptun.localization.preview

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.model.localization.LocalizationDictionary
import hu.kocsisgeri.betterneptun.domain.service.Localization
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json

internal class PreviewLocalizationServiceImpl(context: Context): LocalizationService {

    private val dictionaries = MutableStateFlow(
        loadLocalizationFromAssets(context)
    )

    override val languages = MutableStateFlow(emptyList<Language>())

    override suspend fun changeLanguage(languageKey: String) {
        // NO OP
    }

    override fun localized(key: Localization, ): String {
        val currentLocale = AppCompatDelegate.getApplicationLocales()[0]?.language ?: "hu"
        return dictionaries.value[currentLocale]
            ?.localizations[key.key]
            ?.format(*key.args) ?: key.key
    }


    private fun loadLocalizationFromAssets(context: Context): Map<String, LocalizationDictionary> {
        val jsonString =
            context.assets.open("default_localizations.json")
                .bufferedReader().use { it.readText() }

        val rawMap =
            Json.decodeFromString<Map<String, Map<String, String>>>(jsonString)

        return rawMap.map { (lang, translations) ->
            LocalizationDictionary(
                language = lang,
                localizations = translations
            )
        }.associateBy { it.language }
    }
}
