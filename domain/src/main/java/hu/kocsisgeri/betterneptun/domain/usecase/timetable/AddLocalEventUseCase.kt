package hu.kocsisgeri.betterneptun.domain.usecase.timetable

import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import org.koin.core.annotation.Factory

@Factory
class AddLocalEventUseCase(private val neptunRepository: NeptunRepository) {

    suspend operator fun invoke(event: CalendarItem.LocalEvent) {
        neptunRepository.addLocalEvent(event)
    }
}
