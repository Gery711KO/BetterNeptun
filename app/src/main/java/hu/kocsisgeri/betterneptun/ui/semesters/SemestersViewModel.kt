package hu.kocsisgeri.betterneptun.ui.semesters

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.mikephil.charting.data.*
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map

class SemestersViewModel(
    repo: NeptunRepository
) : ViewModel() {

    private val creditFlow = repo.averages.map {
        when (it) {
            is ApiResult.Error -> ApiResult.Error(it.error)
            is ApiResult.Progress -> ApiResult.Progress(it.percentage)
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
    }
    val credits = creditFlow.asLiveData()

    private val averageFlow = repo.averages.map {
        when(it) {
            is ApiResult.Error -> ApiResult.Error(it.error)
            is ApiResult.Progress -> ApiResult.Progress(it.percentage)
            is ApiResult.Success -> {
                val normalAverages = it.data.mapIndexed { index, model ->
                    Entry(
                        (index + 1).toFloat(),
                        model.normalAverage?.toFloat()?: 0f
                    )
                }

                val comAverages = it.data.mapIndexed { index, model ->
                    Entry(
                        (index + 1).toFloat(),
                        model.commutativeAverage?.toFloat()?: 0f
                    )
                }

                val normalSet = LineDataSet(normalAverages, "Átlagok")
                    .apply {
                        setCircleColor(Color.parseColor("#007541"))
                        lineWidth = 3f
                        color = Color.parseColor("#007541")
                    }
                val comSet = LineDataSet(comAverages, "Kommultatív átlagok")
                    .apply {
                        setCircleColor(Color.parseColor("#096FB3"))
                        lineWidth = 3f
                        color = Color.parseColor("#096FB3")
                    }
                ApiResult.Success(LineData(normalSet, comSet))
            }
        }
    }

    val averages = averageFlow.asLiveData()
}