package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.WeekViewConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.Event
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.TimeSpan
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.metrics.rememberWeekViewMetrics
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.WeekViewStyle
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.defaultWeekViewStyle
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateRange
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.seconds

@Composable
fun WeekBackgroundCompose(
    modifier: Modifier = Modifier,
    dateRange: LocalDateRange,
    timeRange: TimeSpan,
    events: List<Event.Single> = emptyList(),
    allDayEvents: List<Event.AllDay> = emptyList(),
    multiDayEvents: List<Event.MultiDay> = emptyList(),
    eventConfig: EventConfig = EventConfig(),
    weekViewConfig: WeekViewConfig,
    onEventClick: (event: Event) -> Unit = {},
    onEventLongPress: (event: Event) -> Unit = {},
    onTimeSlotClick: (LocalDateTime?) -> Unit = {},
    style: WeekViewStyle = defaultWeekViewStyle(),
) {
    val metrics =
        rememberWeekViewMetrics(dateRange, timeRange, events, weekViewConfig.scalingFactor)
    val scrollState = rememberScrollState()
    val today = LocalDate.now()
    var now by remember { mutableStateOf(LocalDateTime.now().time) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now().time
            delay(1.seconds)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableWidth = maxWidth - metrics.leftOffsetDp
        val dynamicColumnWidthDp =
            if (metrics.columnCount > 0) (availableWidth / metrics.columnCount) else availableWidth

        Column(modifier = Modifier.fillMaxSize()) {
            DayHeaderRow(
                days = metrics.days,
                today = today,
                leftOffsetDp = metrics.leftOffsetDp,
                topOffsetDp = metrics.topOffsetDp,
                columnWidth = dynamicColumnWidthDp,
                style = style,
                highlightCurrentDay = weekViewConfig.highlightCurrentDay,
                eventConfig = eventConfig,
            )

            if (multiDayEvents.isNotEmpty()) {
                MultiDayEventsRow(
                    days = metrics.days,
                    multiDayEvents = multiDayEvents,
                    leftOffsetDp = metrics.leftOffsetDp,
                    columnWidth = dynamicColumnWidthDp,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                )
            }

            if (allDayEvents.isNotEmpty()) {
                AllDayEventsRow(
                    days = metrics.days,
                    allDayEvents = allDayEvents,
                    leftOffsetDp = metrics.leftOffsetDp,
                    columnWidth = dynamicColumnWidthDp,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                )
            }

            Row(modifier = Modifier.weight(1f)) {
                TimeAxisColumn(
                    now = now,
                    timeLabels = metrics.timeLabels,
                    gridStartTime = metrics.gridStartTime,
                    gridEndTime = metrics.effectiveEndTime,
                    rowHeightDp = metrics.rowHeightDp,
                    gridHeightDp = metrics.gridHeightDp,
                    leftOffsetDp = metrics.leftOffsetDp,
                    scrollState = scrollState,
                    showNowIndicator = weekViewConfig.showCurrentTimeIndicator,
                    contentPadding = weekViewConfig.contentPadding,
                    style = style,
                )

                Box(
                    modifier =
                        Modifier
                            .verticalScroll(scrollState)
                            .padding(weekViewConfig.contentPadding)
                            .weight(1f)
                            .height(metrics.gridHeightDp),
                ) {
                    GridCanvas(
                        modifier = Modifier.fillMaxSize(),
                        columnCount = metrics.columnCount,
                        rowHeightDp = metrics.rowHeightDp,
                        totalHours = metrics.totalHours,
                        days = metrics.days,
                        today = today,
                        showNowIndicator = weekViewConfig.showCurrentTimeIndicator,
                        highlightCurrentDay = weekViewConfig.highlightCurrentDay,
                        currentTimeLineOnlyToday = weekViewConfig.currentTimeLineOnlyToday,
                        now = now,
                        gridStartTime = metrics.gridStartTime,
                        effectiveEndTime = metrics.effectiveEndTime,
                        style = style,
                        onSelectionChanged = onTimeSlotClick
                    )
                    EventsPane(
                        days = metrics.days,
                        events = events,
                        eventConfig = eventConfig,
                        onEventClick = onEventClick,
                        onEventLongPress = onEventLongPress,
                        columnWidth = dynamicColumnWidthDp,
                        gridHeightDp = metrics.gridHeightDp,
                        gridStartTime = metrics.gridStartTime,
                        effectiveEndTime = metrics.effectiveEndTime,
                        scalingFactor = weekViewConfig.scalingFactor,
                        style = style,
                    )
                }
            }
        }
    }
}
