package hu.kocsisgeri.betterneptun.core.network.model.neptun

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ReceivedMessageDto(
    val messageId: String,
    val senderUserId: String?,
    val isCurrentUserMessageCreator: Boolean,
    val senderName: String,
    val isSystemMessage: Boolean,
    val subject: String,
    val lastPostDate: LocalDateTime,
    val unreadedPostCount: Int,
    val hasAttachment: Boolean,
    val taskId: String? = null,
    val taskName: String? = null,
    val taskDeadlineType: String? = null,
)
