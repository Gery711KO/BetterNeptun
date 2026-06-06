package hu.kocsisgeri.betterneptun.ui.screen.home.model

import hu.kocsisgeri.betterneptun.common.utils.diffEpochSeconds
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.domain.model.TimeDuration
import kotlinx.datetime.LocalDateTime
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

fun LocalDateTime.getTimeUntil(): TimeDuration {
    val diff = diffEpochSeconds(LocalDateTime.now())

    val hours = diff.seconds.inWholeHours
    val seconds = diff.seconds.inWholeSeconds
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
