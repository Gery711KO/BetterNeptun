package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.common.utils.clockTickFlow
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.AllDayEventsRow
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.DayHeaderRow
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.EventsPane
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.GridCanvas
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.MonthHeaderRow
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.MultiDayEventsRow
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.TimeAxisColumn
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.WeekBackgroundCompose
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components.WeekDataHolder
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.WeekViewConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.TimeSpan
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekData
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.WeekViewActions
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.metrics.rememberWeekViewMetrics
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.defaultWeekViewColors
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.defaultWeekViewStyle
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.time.Duration.Companion.hours

@Composable
fun TimeTableView(
    weekData: (page: Int) -> WeekData,
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
                                        actions.onScalingFactorChange(newScalingFactor)
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                },
    ) {
        val now by remember { clockTickFlow() }
            .collectAsStateWithLifecycle(LocalDateTime.now())

        val style = defaultWeekViewStyle(
            defaultWeekViewColors(
                currentDayText = MaterialTheme.colorScheme.onSurface,
                currentDayBackground = MaterialTheme.colorScheme.secondary,
                dayHeaderText = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                timeLabelTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                gridLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        WeekBackgroundCompose(
            weekViewConfig = activeWeekConfig,
            weekData = weekData,
            topContent = { pagerState ->
                Column {
                    MonthHeaderRow(weekData(pagerState.settledPage).dateRange.start)
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = false,
                        key = { page -> weekData(page).dateRange.toString() }
                    ) { page ->
                        val data = weekData(page)
                        val metrics = metrics(data, activeWeekConfig)
                        val width = WeekDataHolder(
                            week = data,
                            metrics = metrics,
                            maxWidth = this@WeekBackgroundCompose.maxWidth
                        ).getDynamicWidth()

                        Column {
                            DayHeaderRow(
                                days = metrics.days,
                                today = now.date,
                                leftOffsetDp = 0.dp,
                                topOffsetDp = metrics.topOffsetDp,
                                columnWidth = width,
                                style = style,
                                highlightCurrentDay = activeWeekConfig.highlightCurrentDay,
                                eventConfig = eventConfig,
                            )
                            if (data.getMultiDayEvents().isNotEmpty()) {
                                MultiDayEventsRow(
                                    days = metrics.days,
                                    multiDayEvents = data.getMultiDayEvents(),
                                    leftOffsetDp = metrics.leftOffsetDp,
                                    columnWidth = width,
                                    onEventClick = actions.onEventClick,
                                    onEventLongPress = actions.onEventLongPress,
                                )
                            }

                            if (data.getAllDayEvents().isNotEmpty()) {
                                AllDayEventsRow(
                                    days = metrics.days,
                                    allDayEvents = data.getAllDayEvents(),
                                    leftOffsetDp = metrics.leftOffsetDp,
                                    columnWidth = width,
                                    onEventClick = actions.onEventClick,
                                    onEventLongPress = actions.onEventLongPress,
                                )
                            }
                        }
                    }
                }
            },
            sideContent = { scrollState, weekData ->
                TimeAxisColumn(
                    now = now.time,
                    timeLabels = weekData.metrics.timeLabels,
                    gridStartTime = weekData.metrics.gridStartTime,
                    gridEndTime = weekData.metrics.effectiveEndTime,
                    rowHeightDp = weekData.metrics.rowHeightDp,
                    gridHeightDp = weekData.metrics.gridHeightDp,
                    leftOffsetDp = weekData.metrics.leftOffsetDp,
                    scrollState = scrollState,
                    showNowIndicator = activeWeekConfig.showCurrentTimeIndicator,
                    contentPadding = activeWeekConfig.contentPadding,
                    style = style,
                    modifier = Modifier.padding(top = size.height)
                )
            },
            gridContent = { pagerState, scrollState, maxWidth ->
                HorizontalPager(
                    state = pagerState,
                    key = { page -> weekData(page).dateRange.toString() }
                ) { page ->
                    val data = weekData(page)
                    val metrics = metrics(data, activeWeekConfig)
                    val width = WeekDataHolder(
                        week = data,
                        metrics = metrics,
                        maxWidth = maxWidth
                    ).getDynamicWidth()
                    Box(
                        modifier =
                            Modifier
                                .verticalScroll(scrollState)
                                .padding(activeWeekConfig.contentPadding)
                                .weight(1f)
                                .height(metrics.gridHeightDp),
                    ) {
                        GridCanvas(
                            modifier = Modifier.fillMaxSize(),
                            columnCount = metrics.columnCount,
                            rowHeightDp = metrics.rowHeightDp,
                            totalHours = metrics.totalHours,
                            days = metrics.days,
                            today = now.date,
                            showNowIndicator = activeWeekConfig.showCurrentTimeIndicator,
                            highlightCurrentDay = activeWeekConfig.highlightCurrentDay,
                            currentTimeLineOnlyToday = activeWeekConfig.currentTimeLineOnlyToday,
                            now = now.time,
                            gridStartTime = metrics.gridStartTime,
                            effectiveEndTime = metrics.effectiveEndTime,
                            style = style,
                            onZoom = { zoom ->
                                val newScalingFactor = (localScalingFactor * zoom)
                                    .coerceIn(
                                        activeWeekConfig.minScalingFactor,
                                        activeWeekConfig.maxScalingFactor
                                    )

                                if (newScalingFactor != localScalingFactor) {
                                    localScalingFactor = newScalingFactor
                                    actions.onScalingFactorChange(newScalingFactor)
                                }
                            },
                            onSelectionChanged = actions.onTimeSlotClick
                        )
                        EventsPane(
                            days = metrics.days,
                            events = data.getSingleEvents(),
                            eventConfig = eventConfig,
                            onEventClick = actions.onEventClick,
                            onEventLongPress = actions.onEventLongPress,
                            columnWidth = width,
                            gridHeightDp = metrics.gridHeightDp,
                            gridStartTime = metrics.gridStartTime,
                            effectiveEndTime = metrics.effectiveEndTime,
                            scalingFactor = activeWeekConfig.scalingFactor,
                            style = style,
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun metrics(
    currentItem: WeekData,
    config: WeekViewConfig
)  = rememberWeekViewMetrics(
    dateRange = currentItem.dateRange,
    timeRange = currentItem.getTimeSpan() ?: TimeSpan.of(LocalTime(6, 0), 12.hours),
    events = currentItem.getSingleEvents(),
    scalingFactor = config.scalingFactor
)
