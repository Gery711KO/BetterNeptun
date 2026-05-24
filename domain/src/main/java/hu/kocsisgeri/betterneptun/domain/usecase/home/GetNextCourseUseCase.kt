package hu.kocsisgeri.betterneptun.domain.usecase.home

import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetNextCourseUseCase(private val neptunRepository: NeptunRepository) {

    operator fun <T> invoke(map: (CalendarItem) -> T) =
        neptunRepository.events.map { list ->
            val now = LocalDateTime.now()

            list.sortedBy { it.startTime }.firstOrNull { item ->
                item.startTime.isAfter(now)
            }?.let(map)
        }
}
