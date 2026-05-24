package hu.kocsisgeri.betterneptun.domain.usecase.home

import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import java.time.LocalDateTime

@Factory
class GetCurrentCoursesUseCase(private val neptunRepository: NeptunRepository) {

    operator fun <T> invoke(
        map: (CalendarItem) -> T
    ) = neptunRepository.events.map { list ->
        val now = LocalDateTime.now()

        list.sortedBy { it.startTime }.filter {
            it.startTime.isBefore(now) && it.endTime.isAfter(now)
        }.map(map)
    }
}
