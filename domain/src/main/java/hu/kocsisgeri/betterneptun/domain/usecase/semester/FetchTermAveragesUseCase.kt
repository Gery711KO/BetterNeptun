package hu.kocsisgeri.betterneptun.domain.usecase.semester

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.AcademicRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import org.koin.core.annotation.Factory

@Factory
class FetchTermAveragesUseCase(private val academicRepository: AcademicRepository): UseCase() {

    suspend operator fun invoke() = withLock {
        if (academicRepository.averages.value !is ApiResult.Success) {
            academicRepository.fetchTermAverages()
        }
    }
}
