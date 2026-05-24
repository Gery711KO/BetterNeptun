package hu.kocsisgeri.betterneptun.domain.model

data class TimeDuration(
    val value: Int,
    val unit: Unit
) {
    enum class Unit {
        MINUTES, HOURS, DAYS
    }
}
