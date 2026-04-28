package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "message")
data class MessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "message_sender_name") var senderName: String,
    @ColumnInfo(name = "message_subject") var subject: String,
    @ColumnInfo(name = "message_date") var date: LocalDateTime,
    @ColumnInfo(name = "message_is_new") var isNew: Boolean,
)