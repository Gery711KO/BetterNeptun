package hu.kocsisgeri.betterneptun.data.repository.course

import androidx.lifecycle.asLiveData
import hu.kocsisgeri.betterneptun.ui.timetable.model.CalendarEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.time.LocalDateTime

object CourseRepo {
    val courses : MutableStateFlow<List<CalendarEntity.Event>> = MutableStateFlow(listOf())
    val sorted = courses.map { list ->
        list.sortedBy { it.startTime }.firstOrNull {
            it.startTime.isAfter(LocalDateTime.now())
        }
    }.mapNotNull { it }.asLiveData()
    val unreadMessages = MutableStateFlow(0)
}