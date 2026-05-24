package hu.kocsisgeri.betterneptun.core.database.localevents

import hu.kocsisgeri.betterneptun.core.database.localevents.model.LocalEventEntity
import kotlinx.coroutines.flow.Flow

interface LocalEventsDatabase {

    fun getData(): Flow<List<LocalEventEntity>>
    suspend fun insertOne(event: LocalEventEntity)
    suspend fun insertAll(events: List<LocalEventEntity>)
    suspend fun deleteById(id: Long)
    suspend fun deleteAll()
}