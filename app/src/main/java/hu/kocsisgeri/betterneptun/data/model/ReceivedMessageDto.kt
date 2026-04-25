package hu.kocsisgeri.betterneptun.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageListDto(
    val receivedMessages: List<ReceivedMessageDto>
)

@Serializable
data class ReceivedMessageDto(
    val messageId: String,
    val senderUserId: String,
    val isCurrentUserMessageCreator: Boolean,
    val senderName: String,
    val isSystemMessage: Boolean,
    val subject: String,
    val lastPostDate: String,
    val unreadedPostCount: Int,
    val hasAttachment: Boolean,
    val taskId: String? = null,
    val taskName: String? = null,
    val taskDeadlineType: String? = null,
    val uiDisplayState: UiDisplayStateDto
) {
    @Serializable
    data class UiDisplayStateDto(
        val type: Int,
        val reasons: List<String>
    )
}
