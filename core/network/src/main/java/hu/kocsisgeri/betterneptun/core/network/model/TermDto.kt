package hu.kocsisgeri.betterneptun.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TermDto(
    val creditSum: Int,
    val completedCredit: Int,
    val isClosed: Boolean,
    val text: String
)