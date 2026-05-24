package hu.kocsisgeri.betterneptun.domain.usecase.semester

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase

class FetchTermsUseCase(private val neptunRepository: NeptunRepository): UseCase() {

    suspend operator fun invoke() = withLock {
        if (neptunRepository.terms.value !is ApiResult.Success) {
            neptunRepository.fetchTerms()
        }
    }
}
