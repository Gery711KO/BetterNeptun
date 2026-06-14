package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.metrics

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.TimeSpan
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
internal data class WeekViewMetrics(
    val days: List<LocalDate>,
    val columnCount: Int,
    val leftOffsetDp: Dp,
    val topOffsetDp: Dp,
    val effectiveStartTime: LocalTime,
    val effectiveEndTime: LocalTime,
    val gridStartTime: LocalTime,
    val rowHeightDp: Dp,
    val totalHours: Float,
    val gridHeightDp: Dp,
    val timeLabels: List<LocalTime>,
    val visibleTimeSpan: TimeSpan,
)
