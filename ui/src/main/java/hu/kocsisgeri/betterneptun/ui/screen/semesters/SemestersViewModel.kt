package hu.kocsisgeri.betterneptun.ui.screen.semesters

import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermAveragesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterAveragesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterCreditsUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class SemestersViewModel(
    getSemesterCreditsUseCase: GetSemesterCreditsUseCase,
    getSemesterAveragesUseCase: GetSemesterAveragesUseCase,
    private val fetchTermsUseCase: FetchTermsUseCase,
    private val fetchTermAveragesUseCase: FetchTermAveragesUseCase,
) : ComposeViewModel() {

    val credits = getSemesterCreditsUseCase(
        mapEntry = { point, credits ->
            BarEntry(point, credits)
        },
        mapDataSet = { entryPoints, chartLabel, chartColor ->
            BarDataSet(entryPoints, chartLabel.toLabelString()) to chartColor
        }
    ).stateWhileSubscribed(ApiResult.Loading)

    val averages = getSemesterAveragesUseCase(
        mapEntry = { point, average ->
            Entry(point, average)
        },
        mapDataSet = { averageEntries, chartLabel, chartColor ->
            LineDataSet(averageEntries, chartLabel.toLabelString()).apply {
                lineWidth = 3f
            } to chartColor
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
