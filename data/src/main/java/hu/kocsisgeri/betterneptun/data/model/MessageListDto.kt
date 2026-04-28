package hu.kocsisgeri.betterneptun.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageListDto(
    val receivedMessages: List<ReceivedMessageDto>
)