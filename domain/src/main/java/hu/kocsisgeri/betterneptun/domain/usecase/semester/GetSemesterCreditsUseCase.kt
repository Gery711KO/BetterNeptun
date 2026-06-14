package hu.kocsisgeri.betterneptun.domain.usecase.semester

import hu.kocsisgeri.betterneptun.domain.model.ChartColor
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.AcademicRepository
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class GetSemesterCreditsUseCase(private val academicRepository: AcademicRepository) {

    operator fun <S> invoke(
        mapDataSet: (BarData) -> S,
    ) = academicRepository.terms.map {
        when (it) {
            is ApiResult.Error -> it
            is ApiResult.Loading -> it
            is ApiResult.Success -> {
                val credits = it.data.mapIndexed { index, model ->
                    mapDataSet(
                        BarData(
                            title = (index + 1).toString(),
                            bars = listOf(
                                BarData.Bar(
                                    value = (model.semesterTakenCredits?.toDouble() ?: 0.0),
                                    chartLabel = ChartLabel.TakenCredits,
                                    chartColor = ChartColor.Primary,
                                ),
                                BarData.Bar(
                                    value = (model.semesterFulfilledCredits?.toDouble() ?: 0.0),
                                    chartLabel = ChartLabel.FulfilledCredits,
                                    chartColor = ChartColor.Secondary,
                                )
                            )
                        )
                    )
                }
                ApiResult.Success(credits)
            }
        }
    }

    data class BarData(
        val title: String,
        val bars: List<Bar>,
    ) {
        data class Bar(
            val value: Double,
            val chartLabel: ChartLabel,
            val chartColor: ChartColor,
        )
    }

    enum class ChartLabel {
        TakenCredits,
        FulfilledCredits
    }
}
