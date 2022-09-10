package hu.kocsisgeri.betterneptun.ui.model

import hu.kocsisgeri.betterneptun.domain.api.dto.LoginRequestDto

data class NeptunUser(
    val UserLogin: String? = null,
    val Password: String? = null,
    val CurrentPage : Int? = 1,
) {
    fun loginRequestData() : LoginRequestDto {
        return LoginRequestDto(
            user = UserLogin,
            pwd = Password,
            captcha = "",
            GUID = null,
            UserLogin = null
        )
    }
}
