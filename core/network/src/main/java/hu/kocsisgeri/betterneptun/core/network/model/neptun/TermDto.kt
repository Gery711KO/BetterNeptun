package hu.kocsisgeri.betterneptun.core.network.model.neptun

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TermDto(
    @SerialName("value") val id: String,
    val creditSum: Int,
    val completedCredit: Int,
    val isClosed: Boolean,
    val text: String
)
