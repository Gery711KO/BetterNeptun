package hu.kocsisgeri.betterneptun.domain.usecase.subjects

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase

class FetchSubjectsUseCase(private val neptunRepository: NeptunRepository): UseCase() {

    suspend operator fun invoke(termId: String?) = withLock {
        termId?.let {
            neptunRepository.fetchSubjects(termId)
        }
    }
}
