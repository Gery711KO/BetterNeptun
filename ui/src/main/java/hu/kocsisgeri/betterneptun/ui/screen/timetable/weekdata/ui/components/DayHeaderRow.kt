package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.WeekViewStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlin.collections.forEach

private val formatter = LocalDate.Format {
    monthNumber()
    char('/')
    day()
}

@Composable
internal fun DayHeaderRow(
    days: List<LocalDate>,
    today: LocalDate,
    leftOffsetDp: Dp,
    topOffsetDp: Dp,
    columnWidth: Dp,
    style: WeekViewStyle,
    highlightCurrentDay: Boolean = true,
    eventConfig: EventConfig = EventConfig(),
) {
    Row {
        Box(modifier = Modifier.size(leftOffsetDp, topOffsetDp))
        days.forEach { date ->
            val isToday = date == today
            val boxModifier =
                if (highlightCurrentDay && isToday) {
                    Modifier
                        .size(columnWidth, topOffsetDp)
                        .background(style.colors.currentDayBackground)
                        .padding(vertical = 2.dp)
                } else {
                    Modifier
                        .size(columnWidth, topOffsetDp)
                        .padding(vertical = 2.dp)
                }
            val textStyle =
                if (highlightCurrentDay && isToday) {
                    TextStyle(
                        fontSize = 13.sp,
                        color = style.colors.currentDayText,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    TextStyle(
                        fontSize = 13.sp,
                        color = style.colors.dayHeaderText,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            val dayName =
                if (eventConfig.alwaysUseFullName) {
                    date.dayOfWeek.name // TODO
                } else {
                    date.dayOfWeek.name // TODO
                }
            val shortDate = date.format(formatter)
            Column(
                modifier = boxModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = dayName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = shortDate,
                    maxLines = 1,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}