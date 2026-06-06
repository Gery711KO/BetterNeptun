package hu.kocsisgeri.betterneptun.domain.usecase.semester

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.AcademicRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import org.koin.core.annotation.Factory

@Factory
class FetchTermsUseCase(private val academicRepository: AcademicRepository): UseCase() {

    suspend operator fun invoke() = withLock {
        if (academicRepository.terms.value !is ApiResult.Success) {
            academicRepository.fetchTerms()
        }
    }
}
