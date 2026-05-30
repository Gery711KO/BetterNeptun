package hu.kocsisgeri.betterneptun.domain.service

import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import kotlinx.coroutines.flow.StateFlow

/**
 * Service for managing application-wide localization and string translations.
 */
interface LocalizationService {

    /**
     * A [StateFlow] of currently available languages.
     */
    val languages: StateFlow<List<Language>>

    /**
     * Changes the current application language.
     * @param languageKey The unique identifier of the language to switch to (e.g., "en", "hu").
     */
    suspend fun changeLanguage(languageKey: String)

    /**
     * Retrieves the localized string for a given [Localization] key.
     * @param key The [Localization] object containing the key and default value.
     * @param args Optional arguments for string formatting.
     * @return The translated string, or the default value if no translation is found.
     */
    fun localized(key: Localization, vararg args: String): String
}
