package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "local_event")
data class LocalEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "start_time") var startTime: LocalDateTime,
    @ColumnInfo(name = "end_time") var endTime: LocalDateTime,
    @ColumnInfo(name = "location") var location: String,
    @ColumnInfo(name = "color") var color: Int,
)