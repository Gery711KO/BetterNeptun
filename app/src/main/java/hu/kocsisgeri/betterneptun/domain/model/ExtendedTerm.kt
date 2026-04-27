package hu.kocsisgeri.betterneptun.domain.model

import java.time.LocalDateTime

data class ExtendedTerm(
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
