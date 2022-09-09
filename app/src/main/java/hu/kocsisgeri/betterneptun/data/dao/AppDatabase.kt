package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}