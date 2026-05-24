package hu.kocsisgeri.betterneptun.ui.screen.semesters.model

import hu.kocsisgeri.betterneptun.domain.model.ChartColor

data class LineData(
    val label: String,
    val points: List<Double>,
    val color: ChartColor,
)
