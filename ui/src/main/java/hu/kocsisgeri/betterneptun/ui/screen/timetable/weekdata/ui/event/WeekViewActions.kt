package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Immutable
data class WeekViewActions(
    val onEventClick: (event: Event) -> Unit = {},
    val onEventLongPress: (event: Event) -> Unit = {},
    val onScalingFactorChange: (Float) -> Unit = {},
    val onTimeSlotClick: (LocalDateTime?) -> Unit = {},
    val onDateTitleChanged: (LocalDate) -> Unit = {}
)
