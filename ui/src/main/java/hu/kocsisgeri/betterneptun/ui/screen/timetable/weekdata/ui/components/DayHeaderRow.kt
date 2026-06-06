package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.localization.localized
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.config.EventConfig
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.style.WeekViewStyle
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

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
                        .clip(CircleShape)
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

            Column(
                modifier = boxModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = date.dayOfWeek
                        .toLocalizationKey(!eventConfig.alwaysUseFullName)
                        .localized(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text =  date.getFormatted(),
                    maxLines = 1,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun DayOfWeek.toLocalizationKey(abbreviated: Boolean) = when (this) {
    DayOfWeek.MONDAY -> if (abbreviated) LocalizationKey.DAY_MONDAY_SHORT else LocalizationKey.DAY_MONDAY
    DayOfWeek.TUESDAY -> if (abbreviated) LocalizationKey.DAY_TUESDAY_SHORT else LocalizationKey.DAY_TUESDAY
    DayOfWeek.WEDNESDAY -> if (abbreviated) LocalizationKey.DAY_WEDNESDAY_SHORT else LocalizationKey.DAY_WEDNESDAY
    DayOfWeek.THURSDAY -> if (abbreviated) LocalizationKey.DAY_THURSDAY_SHORT else LocalizationKey.DAY_THURSDAY
    DayOfWeek.FRIDAY -> if (abbreviated) LocalizationKey.DAY_FRIDAY_SHORT else LocalizationKey.DAY_FRIDAY
    DayOfWeek.SATURDAY -> if (abbreviated) LocalizationKey.DAY_SATURDAY_SHORT else LocalizationKey.DAY_SATURDAY
    DayOfWeek.SUNDAY -> if (abbreviated) LocalizationKey.DAY_SUNDAY_SHORT else LocalizationKey.DAY_SUNDAY
}

private fun LocalDate.getFormatted() = LocalDate.Format {
    day()
}.format(this)
