package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MessageEntity::class, ColorEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract val messages: MessageDao
    abstract val colors: ColorDao
}