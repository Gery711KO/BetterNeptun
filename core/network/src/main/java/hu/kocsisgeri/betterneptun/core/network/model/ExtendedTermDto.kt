package hu.kocsisgeri.betterneptun.core.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ExtendedTermDto(
    val termDataStatus: String,
    val term: String,
    val termId: String,
    val financialStatus: String,
    val semester: Int,
    @Contextual
    val registration: LocalDateTime,
    @Contextual
    val closing: LocalDateTime,
    val usedSupportedSemestersInTotal: Int,
    val studentTrainingTermDataId: String,
)