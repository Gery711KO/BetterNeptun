package hu.kocsisgeri.betterneptun.domain.usecase.timetable

import hu.kocsisgeri.betterneptun.domain.repository.neptun.CalendarRepository
import org.koin.core.annotation.Factory

@Factory
class GetEventsUseCase(private val calendarRepository: CalendarRepository) {

    operator fun invoke() = calendarRepository.events
}
