package hu.kocsisgeri.betterneptun.ui.model

import hu.kocsisgeri.betterneptun.data.model.AuthenticationRequestDto

data class NeptunUser(
    val UserLogin: String,
    val Password: String,
    val CurrentPage : Int? = 1,
) {
    fun loginRequestData() : AuthenticationRequestDto {
        return AuthenticationRequestDto(
            userName = UserLogin,
            password = Password,
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
