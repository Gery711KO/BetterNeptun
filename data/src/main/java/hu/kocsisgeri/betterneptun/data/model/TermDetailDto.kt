package hu.kocsisgeri.betterneptun.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TermDetailDto(
    val financialStatus: String,
    @Contextual
    val registration: LocalDateTime,
    val averagesCreditIndicies: List<Metric>,
    val furtherHalfYearAverages: List<Metric>,
    val furtherCumulativeAverages: List<Metric>,
    @Contextual
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