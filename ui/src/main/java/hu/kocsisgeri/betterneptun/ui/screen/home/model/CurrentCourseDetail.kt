package hu.kocsisgeri.betterneptun.ui.screen.home.model

data class CurrentCourseDetail(
    val id: Long,
    val title: String,
    val location: String?,
    val progress: Int,
    val remainingTimeMinutes: Int,
    val color: Int,
)