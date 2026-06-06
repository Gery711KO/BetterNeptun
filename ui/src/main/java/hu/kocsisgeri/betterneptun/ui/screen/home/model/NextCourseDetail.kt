package hu.kocsisgeri.betterneptun.ui.screen.home.model

import hu.kocsisgeri.betterneptun.domain.model.TimeDuration
import kotlinx.datetime.LocalDateTime

data class NextCourseDetail(
    val id: Long,
    val title: String,
    val location: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val color: Int,
    val timeUntilEvent: TimeDuration,
)
