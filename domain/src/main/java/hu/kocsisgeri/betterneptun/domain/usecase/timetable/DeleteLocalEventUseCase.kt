package hu.kocsisgeri.betterneptun.domain.usecase.timetable

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository

class DeleteLocalEventUseCase(private val neptunRepository: NeptunRepository) {

    suspend operator fun invoke(eventId: Long) {
        neptunRepository.deleteLocalEvent(eventId)
    }
}