package hu.kocsisgeri.betterneptun.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MessageDetailsDto(
    val messageData: MessageDataDto,
    val recipients: List<RecipientDto>,
    val posts: List<PostDto>
) {
    @Serializable
    data class MessageDataDto(
        val isCurrentUserMessageOwner: Boolean,
        val isSecretMessage: Boolean,
        val isSystemMessage: Boolean,
        val isReplyEnabled: Boolean,
        val description: String,
        @Contextual
        val endDate: LocalDateTime?,
        val subject: String,
        val taskId: String?,
        val taskName: String?,
        val taskDeadlineType: String?
    )

    @Serializable
    data class RecipientDto(
        val userId: String,
        val printName: String
    )

    @Serializable
    data class PostDto(
        val postId: String,
        val parentPostId: String?,
        val senderUserId: String?,
        val plainTextPreview: String?,
        val htmlText: String,
        @Contextual
        val sendDate: LocalDateTime,
        @Contextual
        val expectedAttachmentsDeletionDate: LocalDateTime?,
        val attachments: List<String>, // Adjust type if attachments contain more than strings
        val isRead: Boolean
    )
}
