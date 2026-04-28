package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDateTime
import java.time.ZoneOffset

@Database(entities = [MessageEntity::class, ColorEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val messages: MessageDao
    abstract val colors: ColorDao
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }
}