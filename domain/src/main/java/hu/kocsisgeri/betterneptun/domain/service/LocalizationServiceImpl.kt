package hu.kocsisgeri.betterneptun.domain.service

import android.content.Context
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.model.localization.LocalizationDictionary
import hu.kocsisgeri.betterneptun.domain.repository.localization.LocalizationRepository
import kotlinx.coroutines.flow.MutableStateFlow

internal class LocalizationServiceImpl(
    private val context: Context,
    private val localizationRepository: LocalizationRepository,
): LocalizationService {

    private val allDictionaries = MutableStateFlow(emptyList<LocalizationDictionary>())

    private val currentDictionary = MutableStateFlow(
        LocalizationDictionary(
            language = "",
            localizations = emptyMap()
        )
    )

    override val languages = MutableStateFlow(emptyList<Language>())

    override val isInitialized = MutableStateFlow(false)

    override suspend fun initialize() {
        val availableLanguages = localizationRepository.getLanguages()

        languages.value = availableLanguages
        allDictionaries.value = availableLanguages.map { language ->
            localizationRepository.getLocalizationDictionary(language)
        }

        availableLanguages.find { it.isDefault }?.let { defaultLanguage ->
            changeLanguage(defaultLanguage)
        }

        isInitialized.value = currentDictionary.value.localizations.isNotEmpty()
    }

    override suspend fun changeLanguage(language: Language) {
        languages.value = languages.value.map {
            it.copy(isSelected = it == language)
        }
        allDictionaries.value.find { it.language == language.key }?.let { dictionary ->
            currentDictionary.value = dictionary
        }
    }

    override fun localized(id: Int, vararg args: String): String {
        val key = context.getString(id)
        val localizedString = currentDictionary.value
            .localizations[key]
            ?.format(*args)

        return localizedString?: key
    }

    override fun localized(key: String, vararg args: String): String {
        val localizedString = currentDictionary.value
            .localizations[key]
            ?.format(*args)

        return localizedString?: key
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocalizationServiceImpl) return false

        return currentDictionary.value == other.currentDictionary.value
    }

    override fun hashCode(): Int {
        return currentDictionary.value.hashCode()
    }
}