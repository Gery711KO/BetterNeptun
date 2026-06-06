package hu.kocsisgeri.betterneptun.common.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDate.Companion.now(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDate {
    return Clock.System.now().toLocalDateTime(timeZone).date
}

fun LocalDateTime.plus(duration: Duration): LocalDateTime {
    return toInstant(TimeZone.UTC).plus(duration).toLocalDateTime(TimeZone.UTC)
}

fun LocalDateTime.minus(duration: Duration): LocalDateTime {
    return toInstant(TimeZone.UTC).minus(duration).toLocalDateTime(TimeZone.UTC)
}

fun LocalDateTime.getTimeLeft(): Int {
    val diff = diffEpochSeconds(LocalDateTime.now())
    val seconds = diff.seconds.inWholeSeconds
    val minutes = ceil(seconds / 60f).roundToInt()

    return minutes
}

fun LocalDateTime.diffEpochSeconds(other: LocalDateTime): Long {
    return toInstant(TimeZone.UTC).epochSeconds
        .minus(other.toInstant(TimeZone.UTC).epochSeconds)
}

fun LocalDateTime.isBefore(other: LocalDateTime): Boolean {
    return toInstant(TimeZone.currentSystemDefault()) < other.toInstant(TimeZone.currentSystemDefault())
}

fun LocalDateTime.isAfter(other: LocalDateTime): Boolean {
    return toInstant(TimeZone.currentSystemDefault()) > other.toInstant(TimeZone.currentSystemDefault())
}

fun LocalTime.isBefore(other: LocalTime): Boolean {
    val now = LocalDate.now()
    return atDate(now).isBefore(other.atDate(now))
}

fun LocalTime.isAfter(other: LocalTime): Boolean {
    val now = LocalDate.now()
    return atDate(now).isAfter(other.atDate(now))
}

fun LocalTime.plus(duration: Duration): LocalTime {
    val nanosecondsInDay = 24L * 60 * 60 * 1_000_000_000 // Egy nap nanoms-ban

    val resultNano = (this.toNanosecondOfDay() + duration.inWholeNanoseconds) % nanosecondsInDay

    val finalNano = if (resultNano < 0) resultNano + nanosecondsInDay else resultNano

    return LocalTime.fromNanosecondOfDay(finalNano)
}

fun LocalTime.truncatedToHours(): LocalTime {
    return LocalTime(hour = this.hour, minute = 0, second = 0, nanosecond = 0)
}

val LocalTime.Companion.MAX: LocalTime
    get() = LocalTime(hour = 23, minute = 59, second = 59, nanosecond = 999_999_999)

fun LocalTime.minutesUntil(other: LocalTime): Long {
    val dayInSeconds = 24 * 60 * 60
    val diffInSeconds = (other.toSecondOfDay() - this.toSecondOfDay() + dayInSeconds) % dayInSeconds
    return diffInSeconds / 60L
}
