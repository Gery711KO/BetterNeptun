package hu.kocsisgeri.betterneptun.data.model

data class AuthenticationResponseDto(
    val neptunCode: String,
    val accessToken: String,
    val numberOfDaysUntilADPasswordExpires: Int?,
    val isCaptchaRequired: Boolean,
    val isTwoFactorRequired: Boolean,
    val refreshTokenExpiration: String,
)