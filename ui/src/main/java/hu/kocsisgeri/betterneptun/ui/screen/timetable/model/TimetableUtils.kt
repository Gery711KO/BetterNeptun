package hu.kocsisgeri.betterneptun.ui.screen.timetable.model

import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.util.TimeSpan
import hu.kocsisgeri.betterneptun.domain.model.CalendarItem
import hu.kocsisgeri.betterneptun.ui.theme.Black
import hu.kocsisgeri.betterneptun.ui.theme.White
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

fun CalendarItem.toComposeEvent(): Event.Single {
    return Event.Single(
        id = id,
        date = startTime.toLocalDate(),
        title = title,
        shortTitle = title.take(3),
        subTitle = location,
        timeSpan = TimeSpan(startTime.toLocalTime(), endTime.toLocalTime()),
        textColor = if (isColorDark(color)) White.toArgb() else Black.toArgb(),
        backgroundColor = color
    )
}

fun CalendarItem.getRemainingTime(): Float {
    val diff =
        endTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    return seconds / 60f
}

fun CalendarItem.getTime(): Float {
    val diff = endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    return seconds / 60f
}

fun CalendarItem.getPercent(): Int {
    return (100f - (getRemainingTime() / getTime()) * 100f).roundToInt()
}

fun yearMonthsBetween(startDate: LocalDate, endDate: LocalDate): List<YearMonth> {
    val yearMonths = mutableListOf<YearMonth>()
    val maxYearMonth = endDate.yearMonth
    var currentYearMonth = startDate.yearMonth

    while (currentYearMonth <= maxYearMonth) {
        yearMonths += currentYearMonth
        currentYearMonth = currentYearMonth.plusMonths(1)
    }

    return yearMonths
}

private val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(year, month)

fun isColorDark(color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) < 0.5;
}

