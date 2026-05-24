package hu.kocsisgeri.betterneptun.core.network.model.neptun

import kotlinx.serialization.Serializable

@Serializable
data class TermAveragesDto(
    val defaultTerm: Int,
    val numberOfTerms: Int,
    val terms: List<Term>,
    val termAveragesByTrainings: List<TermAverage>,
    val creditIndexLabel: String?,
    val sumAverageLabel: String?,
    val averageLabel: String?
) {

    @Serializable
    data class Term(
        val value: Int,
        val text: String,
        val isActualTerm: Boolean
    )

    @Serializable
    data class TermAverage(
        val termId: Int,
        val studentTrainingTermId: String,
        val creditIndex: Double,
        val sumAverage: Double,
        val average: Double,
        val canSeeJustClosedTerm: Boolean
    )
}
