package hu.kocsisgeri.betterneptun.domain.usecase.subjects

import hu.kocsisgeri.betterneptun.domain.repository.neptun.AcademicRepository
import org.koin.core.annotation.Factory

@Factory
class GetSubjectsUseCase(private val academicRepository: AcademicRepository) {

    operator fun invoke() = academicRepository.subjects
}
