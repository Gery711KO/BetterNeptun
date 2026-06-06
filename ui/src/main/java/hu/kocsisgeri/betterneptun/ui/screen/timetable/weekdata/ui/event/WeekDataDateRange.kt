package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event

import hu.kocsisgeri.betterneptun.common.utils.plus
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.days

class WeekDataDateRange(
    override val start: LocalDateTime,
    override val endInclusive: LocalDateTime,
) : ClosedRange<LocalDateTime>, Iterable<LocalDateTime> {
    init {
        require(start <= endInclusive) {
            "start ($start) must be <= endInclusive ($endInclusive)"
        }
    }

    override fun contains(value: LocalDateTime): Boolean = value in start..endInclusive

    override fun iterator(): Iterator<LocalDateTime> =
        object : Iterator<LocalDateTime> {
            private var current = start

            override fun hasNext() = current <= endInclusive

            override fun next(): LocalDateTime {
                if (!hasNext()) throw NoSuchElementException()
                val result = current
                current = current.plus(1.days)
                return result
            }
        }
}