package hu.kocsisgeri.betterneptun.localization.service

import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.model.localization.LocalizationDictionary
import hu.kocsisgeri.betterneptun.domain.repository.localization.LocalizationRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.domain.service.Localization
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Singleton

@Singleton
internal class LocalizationServiceImpl(
    private val localizationRepository: LocalizationRepository,
    private val settingsRepository: SettingsRepository,
): LocalizationService, Initializable {

    private val allDictionaries = MutableStateFlow(emptyList<LocalizationDictionary>())

    private val currentDictionary = MutableStateFlow(
        LocalizationDictionary(
            language = "",
            localizations = emptyMap()
        )
    )

    override val languages = MutableStateFlow(emptyList<Language>())

    override val isInitialized = MutableSharedFlow<Boolean>(1, 1)

    override suspend fun initialize() {
        val storedLanguage = settingsRepository.storedLanguage.firstOrNull()
        val availableLanguages = localizationRepository.getLanguages()

        languages.value = availableLanguages
        allDictionaries.value = availableLanguages.map { language ->
            localizationRepository.getLocalizationDictionary(language)
        }

        (storedLanguage?: availableLanguages.find { it.isDefault }?.key)?.let { language ->
            changeLanguage(language)
        }

        isInitialized.tryEmit(currentDictionary.value.localizations.isNotEmpty())
    }

    override suspend fun changeLanguage(languageKey: String) {
        languages.value = languages.value.map {
            it.copy(isSelected = it.key == languageKey)
        }
        allDictionaries.value.find { it.language == languageKey }?.let { dictionary ->
            currentDictionary.value = dictionary
        }
        settingsRepository.saveLanguage(languageKey)
    }

    override fun localized(key: Localization, vararg args: String): String {
        val localizedString = currentDictionary.value
            .localizations[key.key]
            ?.format(*args)

        return localizedString?: key.defaultValue
    }
}

internal fun defaultLocalizationService() = object: LocalizationService {
    override val languages: StateFlow<List<Language>> =
        MutableStateFlow(listOf(Language.DEFAULT))

    override suspend fun changeLanguage(languageKey: String) {}

    override fun localized(key: Localization, vararg args: String): String {
        return key.defaultValue.format(*args)
    }
}
