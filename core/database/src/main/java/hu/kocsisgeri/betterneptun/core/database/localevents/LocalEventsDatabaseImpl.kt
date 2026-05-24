package hu.kocsisgeri.betterneptun.core.database.localevents

import hu.kocsisgeri.betterneptun.core.database.localevents.model.LocalEventEntity
import hu.kocsisgeri.betterneptun.core.database.room.RoomLocalEventDao
import hu.kocsisgeri.betterneptun.core.database.room.RoomLocalEventEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalEventsDatabaseImpl(
    private val localEventDao: RoomLocalEventDao
) : LocalEventsDatabase {

    override fun getData(): Flow<List<LocalEventEntity>> =
        localEventDao.getData().map { list -> list.map { it.toLocalEventEntity() } }

    override suspend fun insertOne(event: LocalEventEntity) = localEventDao
        .insertOne(event.toRoomLocalEventEntity())
    override suspend fun insertAll(events: List<LocalEventEntity>) = localEventDao
        .insertAll(events.map { it.toRoomLocalEventEntity() })

    override suspend fun deleteById(id: Long) = localEventDao.deleteById(id)
    override suspend fun deleteAll() = localEventDao.deleteAll()

    private fun RoomLocalEventEntity.toLocalEventEntity() = LocalEventEntity(
        id = id,
        title = title,
        startTime = startTime,
        endTime = endTime,
        location = location,
        color = color
    )

    private fun LocalEventEntity.toRoomLocalEventEntity() = RoomLocalEventEntity(
        id = id,
        title = title,
        startTime = startTime,
        endTime = endTime,
        location = location,
        color = color
    )
}