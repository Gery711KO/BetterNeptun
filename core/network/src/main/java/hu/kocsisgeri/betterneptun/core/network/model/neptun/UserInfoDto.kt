package hu.kocsisgeri.betterneptun.core.network.model.neptun

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    val userStatus: Int,
    val studentTrainingId: String,
    val userAvatar: Avatar,
    val name: String,
    val neptunCode: String,
    val substitutePrintName: String?,
    val isTokenRegistered: Boolean,
) {

    @Serializable
    data class Avatar(
        val avatarType: Int,
        val image: String?,
        val fallbackColorCodeInHexa: String?,
        val printName: String,
    )
}