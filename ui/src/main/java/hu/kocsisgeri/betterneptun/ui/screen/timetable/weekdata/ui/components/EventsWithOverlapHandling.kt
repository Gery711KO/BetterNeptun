package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.Event
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.util.EventOverlapCalculator
import kotlinx.datetime.LocalTime

@Composable
fun EventsWithOverlapHandling(
    modifier: Modifier = Modifier,
    scalingFactor: Float = 1f,
    events: List<Event.Single>,
    eventConfig: EventConfig,
    startTime: LocalTime,
    endTime: LocalTime,
    columnWidth: Dp,
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
) {
    // Filter events for the current day and time range
    val visibleEvents =
        events.filter { event ->
            // Check if event is within the visible time range
            event.timeSpan.start < endTime && event.timeSpan.endExclusive > startTime
        }

    if (visibleEvents.isEmpty()) return

    // Calculate overlap layouts for all visible events
    val eventLayouts = EventOverlapCalculator.calculateEventLayouts(visibleEvents)

    Box(modifier = modifier) {
        visibleEvents.forEach { event ->
            val layout: EventOverlapCalculator.EventLayout? = eventLayouts[event.id]
            if (layout != null) {
                EventCompose(
                    event = event,
                    scalingFactor = scalingFactor,
                    eventConfig = eventConfig,
                    startTime = startTime,
                    columnWidth = columnWidth,
                    eventLayout = layout,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                )
            }
        }
    }
}