package hu.kocsisgeri.betterneptun.core.network.model.neptun

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TermDetailDto(
    val financialStatus: String,
    val registration: LocalDateTime,
    val averagesCreditIndicies: List<Metric>,
    val furtherHalfYearAverages: List<Metric>,
    val furtherCumulativeAverages: List<Metric>,
    val closingDate: LocalDateTime
) {

    @Serializable
    data class Metric(
        val field: String,
        val required: Boolean,
        val translation: String,
        val value: Double?,
        val description: String? = null
    )
}
