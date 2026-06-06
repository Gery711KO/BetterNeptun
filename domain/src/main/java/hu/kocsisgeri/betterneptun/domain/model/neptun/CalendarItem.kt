package hu.kocsisgeri.betterneptun.domain.model.neptun

import kotlinx.datetime.LocalDateTime

sealed interface CalendarItem {

    val id: Long
    val title: String
    val startTime: LocalDateTime
    val endTime: LocalDateTime
    val location: String?
    val color: Int

    data class Event(
        override val id: Long,
        override val title: String,
        override val startTime: LocalDateTime,
        override val endTime: LocalDateTime,
        override val location: String,
        override val color: Int,
        val courseCode: String,
        val subjectCode : String,
        val teacher: String,
        val isAllDay: Boolean,
        val isCanceled: Boolean
    ) : CalendarItem

    data class LocalEvent(
        override val id: Long,
        override val title: String,
        override val startTime: LocalDateTime,
        override val endTime: LocalDateTime,
        override val location: String,
        override val color: Int,
    ) : CalendarItem
}
