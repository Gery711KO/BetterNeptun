package hu.kocsisgeri.betterneptun.domain.model

sealed interface Avatar {

    data class MonogramAvatar(
        val monogram: String,
        val colorLong: Long,
    ): Avatar

    data class Base64Image(
        val base64ImageString: String
    ): Avatar

    data class UrlImage(
        val imageUrl: String
    ): Avatar

    data object SystemAvatar: Avatar
}