package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.domain.model.CalendarItem
import hu.kocsisgeri.database.room.LocalEventEntity

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