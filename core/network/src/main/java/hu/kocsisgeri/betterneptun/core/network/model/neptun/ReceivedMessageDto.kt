package hu.kocsisgeri.betterneptun.core.network.model.neptun

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ReceivedMessageDto(
    val messageId: String,
    val senderUserId: String?,
    val isCurrentUserMessageCreator: Boolean,
    val senderName: String,
    val isSystemMessage: Boolean,
    val subject: String,
    @Contextual
    val lastPostDate: LocalDateTime,
    val unreadedPostCount: Int,
    val hasAttachment: Boolean,
    val taskId: String? = null,
    val taskName: String? = null,
    val taskDeadlineType: String? = null,
)
