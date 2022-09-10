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

data class MessageReader(
    val UserLogin: String? = null,
    val Password: String? = null,
    val CurrentPage: Int? = 1,
    val PersonMessageId: Int? = null
)

data class Filter(
    val TermID: Int = 0
)

data class AddedSubjectRequest(
    val UserLogin: String? = null,
    val Password: String? = null,
    val TermId : Int? = null,
)

data class MarkBookRequest(
    val UserLogin: String? = null,
    val Password: String? = null,
    val filter: Filter = Filter()
)
