package hu.kocsisgeri.betterneptun.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UnreadMessagesCountDto(
    val count: Int,
)