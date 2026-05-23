package hu.kocsisgeri.betterneptun.ui.core.helper

import hu.kocsisgeri.betterneptun.ui.R
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.roundToInt

fun LocalDateTime.getCourseDateString(
    localize: (Int, Array<String>) -> String,
): String {
    val diff =
        this.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val hours = TimeUnit.MILLISECONDS.toHours(diff * 1000)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    val minutes = ceil(seconds / 60f).roundToInt()
    val days = hours / 24f

    val daysUnit = ceil(days).roundToInt()

    return when {
        minutes < 60 -> localize(R.string.home_next_course_minutes, arrayOf(minutes.toString()))
        hours <= hour -> localize(R.string.home_next_course_hours, arrayOf(hours.toString()))
        days < 1 -> localize(R.string.home_next_course_tomorrow, emptyArray())
        days > 1 -> localize(R.string.home_next_course_days, arrayOf(daysUnit.toString()))
        else -> localize(R.string.home_next_course_days, arrayOf(daysUnit.toString()))
    }
}

fun LocalDateTime.getTimeLeft(
    localize: (Int, Array<String>) -> String,
): String {
    val diff =
        this.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    val minutes = ceil(seconds / 60f).roundToInt()
    return localize(R.string.home_ongoing_course_minutes, arrayOf(minutes.toString()))
}