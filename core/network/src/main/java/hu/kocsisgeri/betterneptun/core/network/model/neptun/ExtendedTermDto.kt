package hu.kocsisgeri.betterneptun.core.network.model.neptun

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ExtendedTermDto(
    val termDataStatus: String,
    val term: String,
    val termId: String,
    val financialStatus: String,
    val semester: Int,
    val registration: LocalDateTime,
    val closing: LocalDateTime,
    val usedSupportedSemestersInTotal: Int,
    val studentTrainingTermDataId: String,
)
