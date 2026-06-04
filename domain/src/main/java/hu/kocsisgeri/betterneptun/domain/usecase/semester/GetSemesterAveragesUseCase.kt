package hu.kocsisgeri.betterneptun.domain.usecase.semester

import hu.kocsisgeri.betterneptun.domain.model.ChartColor
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class GetSemesterAveragesUseCase(private val neptunRepository: NeptunRepository) {

    operator fun <S> invoke(
        mapDataSet: (
            averageEntries: List<Double>,
            chartLabel: ChartLabel,
            chartColor: ChartColor
        ) -> S
    ) = neptunRepository.averages.map {
        when (it) {
            is ApiResult.Error -> ApiResult.Error(it.error)
            is ApiResult.Loading -> ApiResult.Loading
            is ApiResult.Success -> {
                val normalAverages = it.data.map { model ->
                    model.normalAverage ?: 0.0
                }
                val comAverages = it.data.map { model ->
                    model.commutativeAverage ?: 0.0
                }

                val normalSet = mapDataSet(
                    normalAverages,
                    ChartLabel.Averages,
                    ChartColor.Primary
                )
                val comSet = mapDataSet(
                    comAverages,
                    ChartLabel.SumAverages,
                    ChartColor.Secondary
                )
                ApiResult.Success(listOf(normalSet, comSet))
            }
        }
    }

    enum class ChartLabel {
        Averages,
        SumAverages
    }
}
