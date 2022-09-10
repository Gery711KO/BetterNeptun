package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ColorDao {

    @Query("SELECT * FROM color")
    suspend fun getData(): List<Color>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Color>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(color: Color)

    @Query("DELETE FROM color")
    suspend fun deleteAll()
}