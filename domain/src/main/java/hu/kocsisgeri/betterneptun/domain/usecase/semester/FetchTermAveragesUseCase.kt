package hu.kocsisgeri.betterneptun.domain.usecase.semester

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import org.koin.core.annotation.Factory

@Factory
class FetchTermAveragesUseCase(private val neptunRepository: NeptunRepository): UseCase() {

    suspend operator fun invoke() = withLock {
        if (neptunRepository.averages.value !is ApiResult.Success) {
            neptunRepository.fetchTermAverages()
        }
    }
}
