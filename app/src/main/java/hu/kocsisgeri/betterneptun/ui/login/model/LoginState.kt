package hu.kocsisgeri.betterneptun.ui.login.model

import hu.kocsisgeri.betterneptun.domain.model.StudentData

sealed interface LoginState {

    data object Loading: LoginState

    data class Idle(
        val neptunCode: String,
        val password: String,
        val stayLoggedIn: Boolean,
        val isButtonEnabled: Boolean
    ): LoginState

    data object SilentLogin: LoginState

    data class Success(val studentData: StudentData): LoginState

    data class Error(val errorMessage: String): LoginState
}

fun LoginState?.isLoading() = when(this) {
    is LoginState.Error -> false
    is LoginState.Idle -> false
    LoginState.Loading -> true
    LoginState.SilentLogin -> true
    is LoginState.Success -> true
    else -> true
}