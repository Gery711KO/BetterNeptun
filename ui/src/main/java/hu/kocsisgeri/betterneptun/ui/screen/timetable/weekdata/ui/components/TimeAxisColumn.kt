package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.kocsisgeri.betterneptun.common.utils.isAfter
import hu.kocsisgeri.betterneptun.common.utils.isBefore
import hu.kocsisgeri.betterneptun.common.utils.minutesUntil
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.WeekViewStyle
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.defaultWeekViewStyle
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char

val formattedTime = LocalTime.Format {
    hour()
    char(':')
    minute()
}

@Composable
internal fun TimeAxisColumn(
    timeLabels: List<LocalTime>,
    now: LocalTime,
    gridStartTime: LocalTime,
    gridEndTime: LocalTime,
    rowHeightDp: Dp,
    gridHeightDp: Dp,
    leftOffsetDp: Dp,
    scrollState: ScrollState,
    showNowIndicator: Boolean,
    style: WeekViewStyle = defaultWeekViewStyle(),
) {
    Box(
        modifier =
            Modifier
                .width(leftOffsetDp)
                .height(gridHeightDp),
        // Total height of the scrollable grid
    ) {
        // Regular time labels (hours)
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            timeLabels.forEach { timeLabel ->
                Box(modifier = Modifier.size(leftOffsetDp, rowHeightDp)) {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = timeLabel.format(formattedTime),
                        style = TextStyle(fontSize = 12.sp, color = style.colors.timeLabelTextColor),
                    )
                }
            }
        }

        // Current time indicator label (HH:mm)
        if (showNowIndicator && now.isAfter(gridStartTime) && now.isBefore(gridEndTime)) {
            val nowPositionMinutes = gridStartTime.minutesUntil(now)
            val nowPositionDp = (nowPositionMinutes / 60f * rowHeightDp.value).dp
            val density = LocalDensity.current.density

            Box(
                modifier =
                    Modifier
                        .offset(y = nowPositionDp - (scrollState.value / density).dp - 12.dp)
                        .width(leftOffsetDp)
                        .height(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = now.format(formattedTime),
                    style =
                        TextStyle(
                            fontSize = 12.sp,
                            color = style.colors.nowIndicator,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                )
            }
        }
    }
}
