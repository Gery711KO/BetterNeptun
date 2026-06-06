package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekData
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.WeekBackgroundCompose
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.WeekViewConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.TimeSpan
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekViewActions
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.defaultWeekViewColors
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.defaultWeekViewStyle
import kotlinx.datetime.LocalTime
import kotlin.time.Duration.Companion.hours

@Composable
fun TimeTableView(
    weekData: WeekData,
    weekViewConfig: WeekViewConfig,
    modifier: Modifier = Modifier,
    eventConfig: EventConfig = EventConfig(),
    actions: WeekViewActions = WeekViewActions(),
) {
    var localScalingFactor by remember { mutableFloatStateOf(weekViewConfig.scalingFactor) }
    val activeWeekConfig = weekViewConfig.copy(scalingFactor = localScalingFactor)

    Box(
        modifier =
            modifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        do {
                            val event = awaitPointerEvent()
                            if (event.changes.size >= 2) {
                                val zoom = event.calculateZoom()
                                if (zoom != 1f) {
                                    val newScalingFactor =
                                        (localScalingFactor * zoom)
                                            .coerceIn(
                                                activeWeekConfig.minScalingFactor,
                                                activeWeekConfig.maxScalingFactor,
                                            )
                                    if (newScalingFactor != localScalingFactor) {
                                        localScalingFactor = newScalingFactor
                                        actions.onScalingFactorChange?.invoke(newScalingFactor)
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                },
    ) {
        // Render the background grid with integrated events
        WeekBackgroundCompose(
            modifier = Modifier.fillMaxSize(),
            dateRange = weekData.dateRange,
            timeRange =
                weekData.getTimeSpan() ?: TimeSpan.of(
                    LocalTime(6, 0),
                    12.hours
                ),
            events = weekData.getSingleEvents(),
            allDayEvents = weekData.getAllDayEvents(),
            multiDayEvents = weekData.getMultiDayEvents(),
            eventConfig = eventConfig,
            onEventClick = actions.onEventClick,
            onEventLongPress = actions.onEventLongPress,
            weekViewConfig = activeWeekConfig,
            style = defaultWeekViewStyle(
                defaultWeekViewColors(
                    currentDayText = MaterialTheme.colorScheme.onSurface,
                    currentDayBackground = MaterialTheme.colorScheme.secondary,
                    dayHeaderText = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    timeLabelTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    gridLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        )
    }
}
