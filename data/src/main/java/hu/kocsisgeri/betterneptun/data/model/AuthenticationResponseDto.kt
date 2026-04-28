package hu.kocsisgeri.betterneptun.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AuthenticationResponseDto(
    val neptunCode: String,
    val accessToken: String,
    val numberOfDaysUntilADPasswordExpires: Int?,
    val isCaptchaRequired: Boolean,
    val isTwoFactorRequired: Boolean,
    @Contextual
    val refreshTokenExpiration: LocalDateTime,
)