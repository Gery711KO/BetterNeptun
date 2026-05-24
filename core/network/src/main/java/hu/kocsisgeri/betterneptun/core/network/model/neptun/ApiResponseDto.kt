package hu.kocsisgeri.betterneptun.core.network.model.neptun

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T: @Serializable Any>(
    val data: T
)
