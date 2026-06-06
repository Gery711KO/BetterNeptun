package hu.kocsisgeri.betterneptun.domain.usecase.home

import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.repository.neptun.CalendarRepository
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory
import kotlin.time.Clock

@Factory
class GetCurrentCoursesUseCase(private val calendarRepository: CalendarRepository) {

    operator fun <T> invoke(
        map: (CalendarItem) -> T
    ) = calendarRepository.events.map { list ->
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        list.sortedBy { it.startTime }.filter {
            it.startTime < now && it.endTime > now
        }.map(map)
    }
}
