package hu.kocsisgeri.betterneptun.domain.usecase.timetable

import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.repository.neptun.CalendarRepository
import org.koin.core.annotation.Factory

@Factory
class AddLocalEventUseCase(private val calendarRepository: CalendarRepository) {

    suspend operator fun invoke(event: CalendarItem.LocalEvent) {
        calendarRepository.addLocalEvent(event)
    }
}
