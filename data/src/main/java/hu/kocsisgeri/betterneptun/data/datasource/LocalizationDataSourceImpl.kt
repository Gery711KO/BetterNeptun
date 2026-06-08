package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.core.network.api.LocalizationApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton
internal class LocalizationDataSourceImpl(
    private val api: LocalizationApiService,
    private val ioDispatcher: CoroutineDispatcher,
): LocalizationDataSource {
    override suspend fun getLanguages() = withContext(ioDispatcher) {
        api.getLanguages()
    }

    override suspend fun getLocalization(languageKey: String) = withContext(ioDispatcher) {
        api.getLocalization(languageKey)
    }
}
