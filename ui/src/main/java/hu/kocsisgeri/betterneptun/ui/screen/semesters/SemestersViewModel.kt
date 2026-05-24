package hu.kocsisgeri.betterneptun.ui.screen.semesters

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermAveragesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterAveragesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterCreditsUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.ColumnBarData
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.ColumnBars
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.LineData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SemestersViewModel(
    getSemesterCreditsUseCase: GetSemesterCreditsUseCase,
    getSemesterAveragesUseCase: GetSemesterAveragesUseCase,
    private val fetchTermsUseCase: FetchTermsUseCase,
    private val fetchTermAveragesUseCase: FetchTermAveragesUseCase,
) : ComposeViewModel() {

    val credits = getSemesterCreditsUseCase(
        mapDataSet = { barData ->
            ColumnBars(
                barLabel = barData.title,
                bars = barData.bars.map { bar ->
                    ColumnBarData(
                        label = bar.chartLabel.toLabelString(),
                        value = bar.value,
                        color = bar.chartColor
                    )
                }
            )
        }
    ).stateWhileSubscribed(ApiResult.Loading)

    val averages = getSemesterAveragesUseCase(
        mapDataSet = { averageEntries, chartLabel, chartColor ->
            val label = chartLabel.toLabelString()
            LineData(
                label = label,
                points = averageEntries,
                color = chartColor
            )
        }
    ).stateWhileSubscribed(ApiResult.Loading)

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        viewModelScope.launchReportingErrors {
            listOf(
                async { fetchTermsUseCase() },
                async { fetchTermAveragesUseCase() }
            ).awaitAll()
        }
    }

    private fun GetSemesterAveragesUseCase.ChartLabel.toLabelString() = when (this) {
        GetSemesterAveragesUseCase.ChartLabel.Averages -> "Átlagok"
        GetSemesterAveragesUseCase.ChartLabel.SumAverages -> "Kommultatív átlagok"
    }

    private fun GetSemesterCreditsUseCase.ChartLabel.toLabelString() = when (this) {
        GetSemesterCreditsUseCase.ChartLabel.TakenCredits -> "Felvett"
        GetSemesterCreditsUseCase.ChartLabel.FulfilledCredits -> "Teljesitett"
    }
}
