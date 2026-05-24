package hu.kocsisgeri.betterneptun.domain.usecase.subjects

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository

class GetSubjectsUseCase(private val neptunRepository: NeptunRepository) {

    operator fun invoke() = neptunRepository.subjects
}
