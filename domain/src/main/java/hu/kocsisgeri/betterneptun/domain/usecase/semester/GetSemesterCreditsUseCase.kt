package hu.kocsisgeri.betterneptun.domain.usecase.semester

import hu.kocsisgeri.betterneptun.domain.model.ChartColor
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map

class GetSemesterCreditsUseCase(private val neptunRepository: NeptunRepository) {

    operator fun <E, S>invoke(
        mapEntry: (point: Float, credits: Float) -> E,
        mapDataSet: (entryPoints: List<E>, chartLabel: ChartLabel, chartColor: ChartColor) -> S,
    ) = neptunRepository.terms.map {
        when (it) {
            is ApiResult.Error -> ApiResult.Error(it.error)
            is ApiResult.Loading -> ApiResult.Loading
            is ApiResult.Success -> {
                val takenCredits = it.data.mapIndexed { index, model ->
                    mapEntry(
                        (index + 1).toFloat(),
                        model.semesterTakenCredits?.toFloat() ?: 0f
                    )
                }
                val acquiredCredits = it.data.mapIndexed { index, model ->
                    mapEntry(
                        (index + 1).toFloat(),
                        model.semesterFulfilledCredits?.toFloat() ?: 0f
                    )
                }
                val takenSet = mapDataSet(
                    takenCredits,
                    ChartLabel.TakenCredits,
                    ChartColor.Primary
                )
                val acquiredSet = mapDataSet(
                    acquiredCredits,
                    ChartLabel.FulfilledCredits,
                    ChartColor.Secondary
                )
                ApiResult.Success(listOf(takenSet, acquiredSet))
            }
        }
    }

    enum class ChartLabel {
        TakenCredits,
        FulfilledCredits
    }
}
