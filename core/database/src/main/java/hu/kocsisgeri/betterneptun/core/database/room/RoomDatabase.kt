package hu.kocsisgeri.betterneptun.core.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Database(entities = [RoomLocalEventEntity::class], version = 2)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract val localEvents: RoomLocalEventDao
}

internal class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            Instant.fromEpochSeconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toInstant(TimeZone.currentSystemDefault())?.epochSeconds
    }
}
