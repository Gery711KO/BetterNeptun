package hu.kocsisgeri.betterneptun.domain.api.dto

data class LoginRequestDto(
    val user: String?,
    val pwd: String?,
    val GUID: String?,
    val UserLogin: String?,
    val captcha: String?,
)

data class PopupDto(
    val state: String? = "hidden",
    val PopupID: String? = "upLoginWait_popupLoginWait"
)