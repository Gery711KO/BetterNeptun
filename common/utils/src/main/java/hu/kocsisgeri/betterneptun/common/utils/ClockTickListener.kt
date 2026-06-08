package hu.kocsisgeri.betterneptun.common.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

fun clockTickFlow(tickType: TickType = TickType.MINUTE) = flow {
    while(true) {
        val now = LocalDateTime.now()

        val delayMillis = when (tickType) {
            TickType.SECONDS -> {
                (1000 - now.nanosecond / 1_000_000).toLong()
            }
            TickType.MINUTE -> {
                val nextMinute = LocalDateTime(now.date, LocalTime(now.hour, now.minute + 1, 0))
                nextMinute.diffEpochSeconds(now)
            }
            TickType.HOUR -> {
                val nextHour = LocalDateTime(now.date, LocalTime(now.hour + 1, 0, 0))
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
