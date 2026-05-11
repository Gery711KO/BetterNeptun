package hu.kocsisgeri.betterneptun.data.mapper

import hu.kocsisgeri.betterneptun.core.network.model.UserAvatarDto
import hu.kocsisgeri.betterneptun.core.network.model.UserInfoDto
import hu.kocsisgeri.betterneptun.domain.model.Avatar

val format = HexFormat { number { prefix = "0x" } }

fun UserAvatarDto.toAvatarDomain() = image?.let { image ->
    Avatar.Base64Image(base64ImageString = image)
}?: fallbackColorCodeInHexa?.let { hexa ->
    Avatar.MonogramAvatar(
        monogram = printName.split(" ").take(2).joinToString(separator = "") { it.first().toString() },
        colorLong = "0xFF$hexa".hexToLong(format)
    )
} ?: Avatar.SystemAvatar

fun UserInfoDto.Avatar.toAvatarDomain() = image?.let { image ->
    Avatar.Base64Image(base64ImageString = image)
}?: fallbackColorCodeInHexa?.let { hexa ->
    Avatar.MonogramAvatar(
        monogram = printName.split(" ").take(2).joinToString(separator = "") { it.first().toString() },
        colorLong = "0xFF$hexa".hexToLong(format)
    )
} ?: Avatar.SystemAvatar