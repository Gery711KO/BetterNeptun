package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.core.database.localevents.model.LocalEventEntity
import hu.kocsisgeri.betterneptun.domain.model.neptun.CalendarItem

fun LocalEventEntity.toDomain() = CalendarItem.LocalEvent(
    id = id,
    title = title,
    startTime = startTime,
    endTime = endTime,
    location = location,
    color = color,
)

fun CalendarItem.LocalEvent.toEntity() = LocalEventEntity(
    id = id,
    title = title,
    startTime = startTime,
    endTime = endTime,
    location = location,
    color = color,
)
