package hu.kocsisgeri.betterneptun.ui.screen.home.model

import hu.kocsisgeri.betterneptun.domain.model.TimeDuration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.roundToInt

fun LocalDateTime.getTimeUntil(): TimeDuration {
    val diff = toEpochSecond(ZoneOffset.UTC)
        .minus(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

    val hours = TimeUnit.MILLISECONDS.toHours(diff * 1000)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    val minutes = ceil(seconds / 60f).roundToInt()
    val days = hours / 24f

    val daysUnit = ceil(days).roundToInt()

    return when {
        minutes < 60 -> TimeDuration(
            value = minutes,
            unit = TimeDuration.Unit.MINUTES
        )
        hours < 24 -> TimeDuration(
            value = hours.toInt(),
            unit = TimeDuration.Unit.HOURS
        )
        else -> TimeDuration(
            value = daysUnit,
            unit = TimeDuration.Unit.DAYS
        )
    }
}

fun LocalDateTime.getTimeLeft(): Int {
    val diff = toEpochSecond(ZoneOffset.UTC)
        .minus(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    val minutes = ceil(seconds / 60f).roundToInt()

    return minutes
}
