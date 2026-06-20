package hu.kocsisgeri.betterneptun.ui.screen.semesters.model

import hu.kocsisgeri.betterneptun.domain.model.ChartColor
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterCreditsUseCase

data class ColumnBars(
    val barLabel: String,
    val bars: List<ColumnBarData>
)

data class ColumnBarData(
    val label: GetSemesterCreditsUseCase.ChartLabel,
    val value: Double,
    val color: ChartColor
)
