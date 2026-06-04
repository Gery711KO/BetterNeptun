package hu.kocsisgeri.betterneptun.data.repository.localization

import hu.kocsisgeri.betterneptun.data.datasource.LocalizationDataSource
import hu.kocsisgeri.betterneptun.domain.model.localization.Language
import hu.kocsisgeri.betterneptun.domain.model.localization.LocalizationDictionary
import hu.kocsisgeri.betterneptun.domain.repository.localization.LocalizationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton
class LocalizationRepositoryImpl(
    private val localizationDataSource: LocalizationDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : LocalizationRepository {

    override suspend fun getLanguages(): List<Language> = withContext(ioDispatcher) {
        localizationDataSource.getLanguages().map {
            Language(
                key = it.key,
                isSelected = it.isDefault,
                isDefault = it.isDefault,
                localizationKey = "language_${it.key}"
            )
        }
    }

    override suspend fun getLocalizationDictionary(language: Language): LocalizationDictionary =
        withContext(ioDispatcher) {
            LocalizationDictionary(
                language = language.key,
                localizations = localizationDataSource.getLocalization(language.key)
            )
        }
}
