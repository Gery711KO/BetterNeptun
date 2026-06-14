package hu.kocsisgeri.betterneptun.ui.screen.semesters

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.error.ErrorRegistry
import hu.kocsisgeri.betterneptun.domain.error.ErrorSender
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorAction
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.UiResult
import hu.kocsisgeri.betterneptun.domain.model.mapToUiResult
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermAveragesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.FetchTermsUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterAveragesUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterCreditsUseCase
import hu.kocsisgeri.betterneptun.ui.core.ErrorHandlingComposeViewModel
import hu.kocsisgeri.betterneptun.ui.error.fullScreen
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.ColumnBarData
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.ColumnBars
import hu.kocsisgeri.betterneptun.ui.screen.semesters.model.LineData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.withTimeout
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class SemestersViewModel(
    errorRegistry: ErrorRegistry,
    errorSender: ErrorSender,
    getSemesterCreditsUseCase: GetSemesterCreditsUseCase,
    getSemesterAveragesUseCase: GetSemesterAveragesUseCase,
    private val fetchTermsUseCase: FetchTermsUseCase,
    private val fetchTermAveragesUseCase: FetchTermAveragesUseCase,
) : ErrorHandlingComposeViewModel(errorRegistry) {

    private val credits = getSemesterCreditsUseCase(
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

    private val averages = getSemesterAveragesUseCase(
        mapDataSet = { averageEntries, chartLabel, chartColor ->
            val label = chartLabel.toLabelString()
            LineData(
                label = label,
                points = averageEntries,
                color = chartColor
            )
        }
    ).stateWhileSubscribed(ApiResult.Loading)

    val uiState = combine(
        credits.map { it.mapToUiResult() },
        averages.mapNotNull { it.mapToUiResult() },
    ) { credits, averages ->
        credits to averages
    }.stateWhileSubscribed(UiResult.Loading to UiResult.Loading)

    init {
        viewModelScope.launchReportingErrors {
            combine(
                credits.filterNot { it is ApiResult.Loading },
                averages.filterNot { it is ApiResult.Loading }
            ) { credits, averages ->
                listOf(credits, averages)
            }.collect { results ->
                when {
                    results.all { it is ApiResult.Error } -> {
                        errorSender.send(
                            error = ErrorContent.fullScreen(
                                title = "Fetching academic data failed",
                                description = "Something went wrong during credits and averages " +
                                        "data fetching. Please try again.",
                                primaryAction = ErrorAction.Suspend(
                                    label = "Retry",
                                    action = { viewModelScope.async { fetchInitialData() }.await() }
                                ),
                                secondaryAction = ErrorAction.Normal(
                                    label = "Back to Home",
                                    action = ErrorAction.PredefinedAction.NavigateBackToHome
                                )
                            )
                        )
                    }
                    results.any { it is ApiResult.Error } -> {
                        results.filterIsInstance<ApiResult.Error>().forEach { error ->
                            errorSender.send(ErrorContent.Snackbar(error.error))
                        }
                    }
                    else -> {
                        // NO-OP
                    }
                }
            }
        }

        viewModelScope.launchReportingErrors {
            fetchInitialData()
        }
    }

    private suspend fun CoroutineScope.fetchInitialData(): Boolean {
        return listOf(
            async { fetchTermsUseCase() },
            async { fetchTermAveragesUseCase() }
        ).awaitAll().any { it }
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
