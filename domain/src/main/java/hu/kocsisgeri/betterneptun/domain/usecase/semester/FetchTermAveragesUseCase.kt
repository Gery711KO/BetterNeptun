package hu.kocsisgeri.betterneptun.domain.usecase.semester

import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.AcademicRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class FetchTermAveragesUseCase(private val academicRepository: AcademicRepository): UseCase() {

    suspend operator fun invoke() = withReturningLock {
        if (academicRepository.averages.value !is ApiResult.Success) {
            academicRepository.fetchTermAverages()
        }

        academicRepository.averages.first { it !is ApiResult.Loading } is ApiResult.Success
    }
}
