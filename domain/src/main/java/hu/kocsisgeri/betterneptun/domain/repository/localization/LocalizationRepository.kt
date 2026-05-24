package hu.kocsisgeri.betterneptun.domain.repository.localization

import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.model.localization.LocalizationDictionary

/**
 * Repository interface for managing localization data.
 *
 * Provides methods to retrieve supported languages and their corresponding
 * localization dictionaries containing translated strings.
 */
interface LocalizationRepository {

    /**
     * Retrieves the list of available languages supported by the application.
     *
     * @return A list of [Language] objects representing the supported localizations.
     */
    suspend fun getLanguages(): List<Language>

    /**
     * Retrieves the localization dictionary for the specified language.
     *
     * @param language The [Language] for which the translations should be fetched.
     * @return A [LocalizationDictionary] containing the key-value pairs for the requested language.
     */
    suspend fun getLocalizationDictionary(language: Language): LocalizationDictionary
}
