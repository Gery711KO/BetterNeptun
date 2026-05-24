package hu.kocsisgeri.betterneptun.core.database.localevents.model

import java.time.LocalDateTime

data class LocalEventEntity(
    val id: Long,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String,
    val color: Int,
)
