package hu.kocsisgeri.betterneptun.domain.model.neptun

data class Average(
    val index: Int = 0,
    val normalAverage: Double?,
    val commutativeAverage: Double?
)
