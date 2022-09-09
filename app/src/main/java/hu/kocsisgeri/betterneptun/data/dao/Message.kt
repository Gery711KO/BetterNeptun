package hu.kocsisgeri.betterneptun.data.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class Message(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "message_detail") var detail: String,
    @ColumnInfo(name = "message_sender_name") var senderName: String,
    @ColumnInfo(name = "message_subject") var subject: String,
    @ColumnInfo(name = "message_date") var date: String,
    @ColumnInfo(name = "message_is_new") var isNew: Boolean,
)