package hu.kocsisgeri.betterneptun.ui.screen.semesters.model

import hu.kocsisgeri.betterneptun.domain.model.ChartColor
import hu.kocsisgeri.betterneptun.domain.usecase.semester.GetSemesterAveragesUseCase

data class LineData(
    val label: GetSemesterAveragesUseCase.ChartLabel,
    val points: List<Double>,
    val color: ChartColor,
)
