package hu.kocsisgeri.betterneptun.data.repository.neptun

import hu.kocsisgeri.betterneptun.data.datasource.LocalDataSource
import hu.kocsisgeri.betterneptun.data.mapper.toDomain
import hu.kocsisgeri.betterneptun.data.mapper.toEntity
import hu.kocsisgeri.betterneptun.domain.clearable.BaseClearable
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.domain.repository.neptun.CalendarRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton
class CalendarRepositoryImpl internal constructor(
    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : CalendarRepository, BaseClearable() {

    private val remoteEvents = clearableStateFlow<List<CalendarItem.Event>>(listOf())
    private val localEvents = localDataSource.localEventsDb.getData().map { list ->
        list.map { it.toDomain() }
    }

    override val events = combine(remoteEvents, localEvents) { remote, local ->
        remote + local
    }

    override suspend fun fetchCalendarData() {
        // TODO
    }

    override suspend fun addLocalEvent(event: CalendarItem.LocalEvent) {
        withContext(ioDispatcher) {
            localDataSource.localEventsDb.insertOne(event.toEntity())
        }
    }

    override suspend fun deleteLocalEvent(eventId: Long) {
        withContext(ioDispatcher) {
            localDataSource.localEventsDb.deleteById(eventId)
        }
    }
}
