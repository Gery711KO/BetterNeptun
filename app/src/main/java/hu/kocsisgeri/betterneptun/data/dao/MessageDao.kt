package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {

    @Query("SELECT * FROM message")
    suspend fun getData(): List<Message>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<Message>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOne(person: Message)

    @Query("DELETE FROM message")
    suspend fun deleteAll()
}