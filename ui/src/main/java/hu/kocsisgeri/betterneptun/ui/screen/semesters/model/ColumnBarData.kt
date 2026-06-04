package hu.kocsisgeri.betterneptun.ui.screen.semesters.model

import hu.kocsisgeri.betterneptun.domain.model.ChartColor

data class ColumnBars(
    val barLabel: String,
    val bars: List<ColumnBarData>
)

data class ColumnBarData(
    val label: String,
    val value: Double,
    val color: ChartColor
)
