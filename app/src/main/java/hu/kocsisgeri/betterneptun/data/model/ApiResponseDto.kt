package hu.kocsisgeri.betterneptun.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T: @Serializable Any>(
    val data: T
)