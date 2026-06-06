package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event

import androidx.compose.runtime.Immutable
import hu.kocsisgeri.betterneptun.common.utils.isAfter
import hu.kocsisgeri.betterneptun.common.utils.isBefore
import hu.kocsisgeri.betterneptun.common.utils.plus
import kotlinx.datetime.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Immutable
data class TimeSpan(
    val start: LocalTime,
    val endExclusive: LocalTime,
) {
    init {
        require(start.isBefore(endExclusive)) {
            "Start time $start must be before end time $endExclusive!"
        }
    }

    val duration: Duration by lazy {
        val diffInSeconds = endExclusive.toSecondOfDay() - start.toSecondOfDay()
        diffInSeconds.seconds
    }

    /**
     * Returns a sequence of hourly time labels within this time span.
     * Each time is normalized to the hour boundary (minute = 0).
     *
     * Example: TimeSpan from 08:30 to 12:15 would return [08:00, 09:00, 10:00, 11:00, 12:00]
     */
    fun hourlyTimes(): Sequence<LocalTime> =
        sequence {
            var currentHour = start.hour
            val endHour = endExclusive.hour

            // Always yield the starting hour
            yield(LocalTime(currentHour, 0))

            // Generate subsequent hours until we reach the end
            // Use <= to include the hour containing the end time
            while (currentHour < endHour) {
                currentHour++
                if (currentHour <= endHour) {
                    yield(LocalTime(currentHour, 0))
                }
            }
        }

    companion object {
        fun of(
            start: LocalTime,
            duration: Duration,
        ): TimeSpan {
            require(!duration.isNegative() && duration != Duration.ZERO) {
                "Duration must be positive, but was $duration"
            }
            val end = start.plus(duration)
            require(end.isAfter(start)) {
                "TimeSpan starting at $start with duration $duration would cross midnight, which is not supported"
            }
            return TimeSpan(start, end)
        }
    }
}
