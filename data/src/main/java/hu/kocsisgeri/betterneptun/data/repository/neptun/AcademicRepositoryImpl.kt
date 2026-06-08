package hu.kocsisgeri.betterneptun.data.repository.neptun

import hu.kocsisgeri.betterneptun.data.datasource.NetworkDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toAverageDomain
import hu.kocsisgeri.betterneptun.data.mapper.toSubjectDomain
import hu.kocsisgeri.betterneptun.data.mapper.toTermDomain
import hu.kocsisgeri.betterneptun.data.util.runApiCall
import hu.kocsisgeri.betterneptun.domain.clearable.BaseClearable
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.Average
import hu.kocsisgeri.betterneptun.domain.model.neptun.Subject
import hu.kocsisgeri.betterneptun.domain.model.neptun.Term
import hu.kocsisgeri.betterneptun.domain.repository.neptun.AcademicRepository
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Singleton

@Singleton
internal class AcademicRepositoryImpl internal constructor(
    private val networkDataSource: NetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : AcademicRepository, BaseClearable() {

    override val subjects = clearableStateFlow<ApiResult<List<Subject>>>(ApiResult.Loading)
    override val terms = clearableStateFlow<ApiResult<List<Term>>>(ApiResult.Loading)
    override val averages = clearableStateFlow<ApiResult<List<Average>>>(ApiResult.Loading)

    override suspend fun fetchSubjects(termId: String) {
        subjects.runApiCall(ioDispatcher) {
            networkDataSource.getTakenSubjects(termId).data.toSubjectDomain()
        }
    }

    override suspend fun fetchTerms() {
        terms.runApiCall(ioDispatcher) {
            networkDataSource.getTerms().data.toTermDomain()
        }
    }

    override suspend fun fetchTermAverages() {
        averages.runApiCall(ioDispatcher) {
            networkDataSource.getTermAverages()
                .data
                .termAveragesByTrainings
                .toAverageDomain()
        }
    }
}
