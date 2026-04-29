package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalEventDao {

    @Query("SELECT * FROM local_event")
    fun getData(): Flow<List<LocalEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(event: LocalEventEntity)

    @Query("DELETE FROM local_event WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM local_event")
    suspend fun deleteAll()
}