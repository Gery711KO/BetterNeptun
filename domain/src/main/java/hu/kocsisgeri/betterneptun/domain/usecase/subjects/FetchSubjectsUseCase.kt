package hu.kocsisgeri.betterneptun.domain.usecase.subjects

import hu.kocsisgeri.betterneptun.domain.repository.neptun.AcademicRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import org.koin.core.annotation.Factory

@Factory
class FetchSubjectsUseCase(private val academicRepository: AcademicRepository): UseCase() {

    suspend operator fun invoke(termId: String?) = withLock {
        termId?.let {
            academicRepository.fetchSubjects(termId)
        }
    }
}
