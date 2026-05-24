package hu.kocsisgeri.betterneptun.core.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface RoomLocalEventDao {

    @Query("SELECT * FROM local_event")
    fun getData(): Flow<List<RoomLocalEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(event: RoomLocalEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<RoomLocalEventEntity>)

    @Query("DELETE FROM local_event WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM local_event")
    suspend fun deleteAll()
}
