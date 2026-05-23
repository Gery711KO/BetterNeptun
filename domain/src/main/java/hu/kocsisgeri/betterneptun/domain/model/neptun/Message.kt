package hu.kocsisgeri.betterneptun.domain.model.neptun

import java.time.LocalDateTime

data class Message(
    val id: String,
    val name: String,
    val subject: String,
    val date: LocalDateTime,
    val isNew : Boolean,
    val senderAvatar: Avatar,
    val messageDetail: MessageDetail? = null
)
