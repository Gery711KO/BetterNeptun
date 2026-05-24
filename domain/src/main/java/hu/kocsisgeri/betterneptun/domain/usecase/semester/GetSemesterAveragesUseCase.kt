package hu.kocsisgeri.betterneptun.domain.usecase.semester

import hu.kocsisgeri.betterneptun.domain.model.ChartColor
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map

class GetSemesterAveragesUseCase(private val neptunRepository: NeptunRepository) {

    operator fun <E, S>invoke(
        mapEntry: (point: Float, average: Float) -> E,
        mapDataSet: (averageEntries: List<E>, chartLabel: ChartLabel, chartColor: ChartColor) -> S
    ) = neptunRepository.averages.map {
        when(it) {
            is ApiResult.Error -> ApiResult.Error(it.error)
            is ApiResult.Loading -> ApiResult.Loading
            is ApiResult.Success -> {
                val normalAverages = it.data.map { model ->
                    mapEntry(
                        (model.index + 1).toFloat(),
                        model.normalAverage?.toFloat()?: 0f,
                    )
                }
                val comAverages = it.data.map { model ->
                    mapEntry(
                        (model.index + 1).toFloat(),
                        model.commutativeAverage?.toFloat()?: 0f,
                    )
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
