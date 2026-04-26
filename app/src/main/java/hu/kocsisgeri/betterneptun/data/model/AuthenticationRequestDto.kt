package hu.kocsisgeri.betterneptun.data.model

data class AuthenticationRequestDto(
    val userName: String,
    val password: String,
    val LCID: Int = 1038,
    val captcha: String = "",
    val captchaIdentifier: String = "",
    val token: String = ""
)