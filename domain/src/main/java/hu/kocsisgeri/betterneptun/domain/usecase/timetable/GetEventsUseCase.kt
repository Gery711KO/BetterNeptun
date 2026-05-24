package hu.kocsisgeri.betterneptun.domain.usecase.timetable

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository

class GetEventsUseCase(private val neptunRepository: NeptunRepository) {

    operator fun invoke() = neptunRepository.events
}
