package hu.kocsisgeri.betterneptun.common.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun clockTickFlow(tickType: TickType = TickType.MINUTE) = flow {
    while(true) {
        val now = LocalDateTime.now()

        val delayMillis = when (tickType) {
            TickType.SECONDS -> {
                (1000 - now.nanosecond / 1_000_000).toLong()
            }
            TickType.MINUTE -> {
                val nextMinute = LocalDateTime(
                    date = now.date,
                    time = LocalTime(now.hour, now.minute, 0).plus(1.minutes)
                )
                nextMinute.diffEpochSeconds(now)
            }
            TickType.HOUR -> {
                val nextHour = LocalDateTime(
                    date = now.date,
                    time = LocalTime(now.hour, 0, 0).plus(1.hours)
                )
                nextHour.diffEpochSeconds(now)
            }
        }

        val safeDelay = if (delayMillis > 0) delayMillis else 1L

        delay(safeDelay)

        emit(LocalDateTime.now())
    }
}

enum class TickType {
    HOUR, MINUTE, SECONDS
}
