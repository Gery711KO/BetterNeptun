package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.Event
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.TimeSpan
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.util.EventOverlapCalculator
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.util.EventPositionUtil
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.time.Duration.Companion.minutes

@Composable
fun EventCompose(
    modifier: Modifier = Modifier,
    event: Event.Single,
    scalingFactor: Float,
    eventConfig: EventConfig,
    startTime: LocalTime,
    columnWidth: Dp,
    eventLayout: EventOverlapCalculator.EventLayout,
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
) {
    val (topOffset, eventHeight) =
        EventPositionUtil.calculateVerticalOffsets(
            event = event,
            startTime = startTime,
            scalingFactor = scalingFactor,
        )

    // Apply overlap layout calculations
    val eventWidth = columnWidth * eventLayout.widthFraction
    val horizontalOffset = columnWidth * eventLayout.offsetFraction

    // Event styling
    val backgroundColor = Color(event.backgroundColor)
    val textColor = Color(event.textColor)
    val cornerRadius = 4.dp

    // Determine which title to show based on config and orientation
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val displayTitle =
        if (eventConfig.alwaysUseFullName) {
            event.title
        } else {
            if (isPortrait) {
                event.shortTitle.ifBlank { event.title }
            } else {
                event.title
            }
        }

    Box(
        modifier =
            modifier
                .testTag("EventView_${event.id}")
                .offset(x = horizontalOffset, y = topOffset)
                .size(width = eventWidth, height = eventHeight)
                .let { if (eventConfig.eventSpacingDp > 0) it.padding(eventConfig.eventSpacingDp.dp) else it }
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .pointerInput(event.id) {
                    detectTapGestures(
                        onTap = { onEventClick?.invoke(event) },
                        onLongPress = { onEventLongPress?.invoke(event) },
                    )
                }
                .padding(start = 4.dp, top = 4.dp, end = 4.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .testTag("EventViewInner_${event.id}"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            // Main title
            Text(
                text = displayTitle,
                color = textColor,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = if (eventConfig.showSubtitle || eventConfig.showTimeEnd) 1 else 2,
                overflow = TextOverflow.Ellipsis,
            )

            // Subtitle (if enabled and available)
            if (eventConfig.showSubtitle && event.subTitle?.isNotBlank() == true) {
                Text(
                    text = event.subTitle,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Time information (if enabled)
            if (eventConfig.showTimeEnd) {
                val timeText = "${event.timeSpan.start} - ${event.timeSpan.endExclusive}"

                Text(
                    text = timeText,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Upper text (if enabled and available)
            if (eventConfig.showUpperText && event.upperText?.isNotBlank() == true) {
                Text(
                    text = event.upperText,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Lower text (if enabled and available)
            if (eventConfig.showLowerText && event.lowerText?.isNotBlank() == true) {
                Text(
                    text = event.lowerText,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventCompose() {
    Column(
        Modifier
            .height(400.dp)
            .width(400.dp),
    ) {
        Text("Preview")

        val event =
            Event.Single(
                id = 1L,
                date = LocalDate.now(),
                title = "Full Title",
                shortTitle = "Short Title",
                subTitle = "Subtitle",
                timeSpan = TimeSpan.of(LocalTime(8, 15), 45.minutes),
                backgroundColor = "#90323D".toColorInt(),
                textColor = "#dddddd".toColorInt(),
                upperText = "Upper Text",
                lowerText = "Lower Text",
            )
        val eventConfig =
            EventConfig(
                showSubtitle = true,
                showTimeEnd = true,
                alwaysUseFullName = false,
                showTimeStart = true,
                showUpperText = true,
                showLowerText = true,
            )
        val eventLayout =
            EventOverlapCalculator.EventLayout(
                widthFraction = 1f,
                offsetFraction = 0f,
                overlapGroup = 0,
            )
        EventCompose(
            modifier =
                Modifier
                    .width(150.dp)
                    .height(50.dp),
            event = event,
            scalingFactor = 1f,
            eventConfig = eventConfig,
            startTime = LocalTime(8, 0),
            columnWidth = 120.dp,
            eventLayout = eventLayout,
        )
    }
}
