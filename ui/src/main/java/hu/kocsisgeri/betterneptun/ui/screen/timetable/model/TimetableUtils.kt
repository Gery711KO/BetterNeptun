package hu.kocsisgeri.betterneptun.ui.screen.timetable.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.Event
import hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event.TimeSpan
import hu.kocsisgeri.betterneptun.ui.theme.isColorDark
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.time.Clock

fun CalendarItem.toComposeEvent(): Event.Single {
    return Event.Single(
        id = id,
        date = startTime.date,
        title = title,
        shortTitle = title.take(3),
        subTitle = location,
        timeSpan = TimeSpan(startTime.time, endTime.time),
        textColor = if (isColorDark(color)) Color.White.toArgb() else Color.Black.toArgb(),
        backgroundColor = color
    )
}

fun CalendarItem.getRemainingTime(): Float {
    val diff = endTime.toInstant(TimeZone.UTC).epochSeconds - Clock.System.now().epochSeconds

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    return seconds / 60f
}

fun CalendarItem.getTime(): Float {
    val diff = endTime.toInstant(TimeZone.UTC).epochSeconds
        .minus(startTime.toInstant(TimeZone.UTC).epochSeconds)

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    return seconds / 60f
}

fun CalendarItem.getPercent(): Int {
    return (100f - (getRemainingTime() / getTime()) * 100f).roundToInt()
}
