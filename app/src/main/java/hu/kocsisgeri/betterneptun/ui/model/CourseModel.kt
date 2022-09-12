package hu.kocsisgeri.betterneptun.ui.model

import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import java.io.Serializable

data class CourseModel(
    val event: CalendarEntity.Event
) : Serializable