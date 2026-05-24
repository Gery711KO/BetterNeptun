package hu.kocsisgeri.betterneptun.domain.usecase.subjects

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import org.koin.core.annotation.Factory

@Factory
class GetSubjectsUseCase(private val neptunRepository: NeptunRepository) {

    operator fun invoke() = neptunRepository.subjects
}
