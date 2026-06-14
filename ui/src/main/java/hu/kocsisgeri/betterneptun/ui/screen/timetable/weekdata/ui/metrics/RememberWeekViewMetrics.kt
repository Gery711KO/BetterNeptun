package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.metrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import hu.kocsisgeri.betterneptun.common.utils.MAX
import hu.kocsisgeri.betterneptun.common.utils.isAfter
import hu.kocsisgeri.betterneptun.common.utils.isBefore
import hu.kocsisgeri.betterneptun.common.utils.truncatedToHours
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.Event
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.TimeSpan
import kotlinx.datetime.LocalDateRange
import kotlinx.datetime.LocalTime

@Composable
internal fun rememberWeekViewMetrics(
    dateRange: LocalDateRange,
    timeRange: TimeSpan,
    events: List<Event.Single>,
    scalingFactor: Float,
): WeekViewMetrics {
    // Derive stable keys from the event list so remember uses value equality (LocalTime)
    // instead of list reference equality which changes every recomposition
    val earliestEventStart = events.minOfOrNull { it.timeSpan.start } ?: timeRange.start
    val latestEventEnd = events.maxOfOrNull { it.timeSpan.endExclusive } ?: timeRange.endExclusive

    return remember(dateRange, timeRange, earliestEventStart, latestEventEnd, scalingFactor) {
        val days = dateRange.toList()
        val columnCount = days.size
        val leftOffsetDp = 48.dp
        val topOffsetDp = 36.dp

        val effectiveStartTime = if (earliestEventStart.isBefore(timeRange.start)) earliestEventStart else timeRange.start

        val rowHeightDp = 60.dp * scalingFactor

        val gridEndTime =
            if (latestEventEnd.hour < 23) {
                LocalTime(latestEventEnd.hour + 1, 0)
            } else {
                LocalTime.MAX
            }
        val effectiveEndTime = if (gridEndTime.isAfter(timeRange.endExclusive)) gridEndTime else timeRange.endExclusive

        val gridStartTime = effectiveStartTime.truncatedToHours()
        val visibleTimeSpan = TimeSpan(gridStartTime, effectiveEndTime)

        val totalHours = visibleTimeSpan.duration.inWholeHours.toFloat() + (visibleTimeSpan.duration.inWholeMinutes % 60 / 60f)
        val gridHeightDp = rowHeightDp * totalHours
        val timeLabels = visibleTimeSpan.hourlyTimes().toList()

        WeekViewMetrics(
            days = days,
            columnCount = columnCount,
            leftOffsetDp = leftOffsetDp,
            topOffsetDp = topOffsetDp,
            effectiveStartTime = effectiveStartTime,
            effectiveEndTime = effectiveEndTime,
            gridStartTime = gridStartTime,
            rowHeightDp = rowHeightDp,
            totalHours = totalHours,
            gridHeightDp = gridHeightDp,
            timeLabels = timeLabels,
            visibleTimeSpan = visibleTimeSpan,
        )
    }
}
