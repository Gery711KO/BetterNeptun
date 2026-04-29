package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.data.dao.LocalEventEntity
import hu.kocsisgeri.betterneptun.domain.model.CalendarItem

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