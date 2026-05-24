package hu.kocsisgeri.betterneptun.domain.usecase.timetable

import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteLocalEventUseCase(private val neptunRepository: NeptunRepository) {

    suspend operator fun invoke(eventId: Long) {
        neptunRepository.deleteLocalEvent(eventId)
    }
}