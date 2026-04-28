package hu.kocsisgeri.betterneptun.domain.model

import java.time.LocalDateTime

sealed class CalendarEntity {

    data class Event(
        val id: Long,
        val title: CharSequence,
        val courseCode: String,
        val subjectCode : String,
        val teacher: String,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val location: CharSequence,
        val color: Int,
        val isAllDay: Boolean,
        val isCanceled: Boolean
    ) : CalendarEntity()

    data class BlockedTimeSlot(
        val id: Long,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime
    ) : CalendarEntity()
}
