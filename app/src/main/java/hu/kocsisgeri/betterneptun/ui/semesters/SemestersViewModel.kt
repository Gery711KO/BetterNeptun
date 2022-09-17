package hu.kocsisgeri.betterneptun.ui.semesters

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.data.repository.neptun.NeptunRepository
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
                        index.toFloat(),
                        model.semesterTakenCredits?.toFloat() ?: 0f
                    )
                }
                val aquiredCredits = it.data.mapIndexed { index, model ->
                    BarEntry(
                        index.toFloat(),
                        model.semesterFulfilledCredits?.toFloat() ?: 0f
                    )
                }
                val takenSet = BarDataSet(takenCredits, "Felvett")
                    .apply { color = Color.parseColor("#99007541") }
                val aquiredSet = BarDataSet(aquiredCredits, "Teljesitett")
                    .apply { color = Color.parseColor("#007541") }
                ApiResult.Success(BarData(takenSet, aquiredSet))
            }
        }
    }
    val credits = creditFlow.asLiveData()
}