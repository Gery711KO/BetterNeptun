package hu.kocsisgeri.betterneptun.ui.screen.home.model

import java.time.LocalDateTime

data class NextCourseDetail(
    val title: String,
    val location: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val color: Int,
    val timeUntilEvent: String,
)