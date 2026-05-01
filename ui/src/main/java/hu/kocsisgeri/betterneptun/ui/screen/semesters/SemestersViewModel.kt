package hu.kocsisgeri.betterneptun.ui.screen.semesters

import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import hu.kocsisgeri.betterneptun.common.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.ui.base.ComposeViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class SemestersViewModel(
    repo: NeptunRepository
) : ComposeViewModel() {

    val credits = repo.terms.map {
        when (it) {
            is ApiResult.Error -> ApiResult.Error(it.error)
            is ApiResult.Loading -> ApiResult.Loading
            is ApiResult.Success -> {
                val takenCredits = it.data.mapIndexed { index, model ->
                    BarEntry(
                        (index + 1).toFloat(),
                        model.semesterTakenCredits?.toFloat() ?: 0f
                    )
                }
                val acquiredCredits = it.data.mapIndexed { index, model ->
                    BarEntry(
                        (index + 1).toFloat(),
                        model.semesterFulfilledCredits?.toFloat() ?: 0f
                    )
                }
                val takenSet = BarDataSet(takenCredits, "Felvett")
                val aquiredSet = BarDataSet(acquiredCredits, "Teljesitett")
                ApiResult.Success(Pair(takenSet, aquiredSet))
            }
        }
    }.stateWhileSubscribed(ApiResult.Loading)

    val averages = repo.averages.map {
        when(it) {
            is ApiResult.Error -> ApiResult.Error(it.error)
            is ApiResult.Loading -> ApiResult.Loading
            is ApiResult.Success -> {
                val normalAverages = it.data.map { model ->
                    Entry(
                        (model.index + 1).toFloat(),
                        model.normalAverage?.toFloat()?: 0f
                    )
                }

                val comAverages = it.data.map { model ->
                    Entry(
                        (model.index + 1).toFloat(),
                        model.commutativeAverage?.toFloat()?: 0f
                    )
                }

                val normalSet = LineDataSet(normalAverages, "Átlagok")
                    .apply {
                        setCircleColor("#007541".toColorInt())
                        lineWidth = 3f
                        color = "#007541".toColorInt()
                    }
                val comSet = LineDataSet(comAverages, "Kommultatív átlagok")
                    .apply {
                        setCircleColor("#096FB3".toColorInt())
                        lineWidth = 3f
                        color = "#096FB3".toColorInt()
                    }
                ApiResult.Success(LineData(normalSet, comSet))
            }
        }
    }.onEach {
        Timber.tag("KURVA").d("$it")
    }
        .stateWhileSubscribed(ApiResult.Loading)

    init {
        viewModelScope.launchReportingErrors {
            repo.fetchTerms()
        }

        viewModelScope.launchReportingErrors {
            repo.fetchTermAverages()
        }
    }
}