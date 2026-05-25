package hu.kocsisgeri.betterneptun.ui.screen.home.model

import hu.kocsisgeri.betterneptun.domain.model.TimeDuration
import java.time.LocalDateTime

data class NextCourseDetail(
    val title: String,
    val location: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val color: Int,
    val timeUntilEvent: TimeDuration,
)