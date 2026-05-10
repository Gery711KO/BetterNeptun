package hu.kocsisgeri.betterneptun.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAvatarDto(
    val userId: String,
    val avatarType: Int,
    val image: String?,
    val fallbackColorCodeInHexa: String?,
    val printName: String,
)