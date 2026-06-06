package hu.kocsisgeri.betterneptun.domain.usecase.timetable

import hu.kocsisgeri.betterneptun.domain.repository.neptun.CalendarRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteLocalEventUseCase(private val calendarRepository: CalendarRepository) {

    suspend operator fun invoke(eventId: Long) {
        calendarRepository.deleteLocalEvent(eventId)
    }
}
