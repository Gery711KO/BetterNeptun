package hu.kocsisgeri.betterneptun.ui.screen.timetable.weekdata.ui.event

data class WeekViewActions(
    val onEventClick: ((event: Event) -> Unit)? = null,
    val onEventLongPress: ((event: Event) -> Unit)? = null,
    val onScalingFactorChange: ((Float) -> Unit)? = null,
)
