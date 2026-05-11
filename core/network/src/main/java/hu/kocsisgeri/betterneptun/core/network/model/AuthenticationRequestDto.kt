package hu.kocsisgeri.betterneptun.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationRequestDto(
    val userName: String,
    val password: String,
    val LCID: Int = 1038,
    val captcha: String = "",
    val captchaIdentifier: String = "",
    val token: String = ""
)