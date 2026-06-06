package hu.kocsisgeri.betterneptun.domain.repository.neptun

import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository responsible for managing calendar events, including both remote Neptun events and local user events.
 */
interface CalendarRepository {
    /**
     * A [Flow] emitting the current list of calendar items, including classes, exams, and local events.
     */
    val events: Flow<List<CalendarItem>>

    /**
     * Refetches calendar data from the remote source.
     */
    suspend fun fetchCalendarData()

    /**
     * Adds a new local event to the calendar.
     *
     * @param event The [CalendarItem.LocalEvent] to be added.
     */
    suspend fun addLocalEvent(event: CalendarItem.LocalEvent)

    /**
     * Deletes a local event from the calendar.
     *
     * @param eventId The unique identifier of the local event to be deleted.
     */
    suspend fun deleteLocalEvent(eventId: Long)
}
