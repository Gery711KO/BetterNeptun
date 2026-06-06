package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.Event
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.WeekViewStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.collections.forEachIndexed

@Composable
internal fun EventsPane(
    days: List<LocalDate>,
    events: List<Event.Single>,
    eventConfig: EventConfig,
    onEventClick: ((event: Event) -> Unit)?,
    onEventLongPress: ((event: Event) -> Unit)?,
    columnWidth: Dp,
    gridHeightDp: Dp,
    gridStartTime: LocalTime,
    effectiveEndTime: LocalTime,
    scalingFactor: Float,
    style: WeekViewStyle,
) {
    days.forEachIndexed { dayIndex, date ->
        val eventsForDay = events.filter { it.date == date }
        if (eventsForDay.isNotEmpty()) {
            Box(
                modifier =
                    Modifier
                        .offset(x = dayIndex * columnWidth)
                        .size(columnWidth, gridHeightDp),
                // Height must be total grid height for proper event positioning
            ) {
                EventsWithOverlapHandling(
                    events = eventsForDay,
                    scalingFactor = scalingFactor,
                    eventConfig = eventConfig,
                    startTime = gridStartTime,
                    endTime = effectiveEndTime,
                    columnWidth = columnWidth,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                    // style = style // Pass style to EventsWithOverlapHandling if it needs it in the future
                )
            }
        }
    }
}