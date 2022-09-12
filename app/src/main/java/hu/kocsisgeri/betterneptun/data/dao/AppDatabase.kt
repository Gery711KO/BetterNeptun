package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class, Color::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedMessages(): MessageDao
    abstract fun savedCourseColors(): ColorDao

}