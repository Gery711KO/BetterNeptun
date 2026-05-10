package hu.kocsisgeri.betterneptun.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PostIdsRequestDto(val postIds: List<String>)