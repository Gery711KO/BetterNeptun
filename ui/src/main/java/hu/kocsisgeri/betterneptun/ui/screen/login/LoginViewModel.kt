package hu.kocsisgeri.betterneptun.ui.screen.login

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.error.ErrorRegistry
import hu.kocsisgeri.betterneptun.domain.usecase.auth.LoginUseCase
import hu.kocsisgeri.betterneptun.ui.core.ErrorHandlingComposeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.login.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    errorRegistry: ErrorRegistry,
) : ErrorHandlingComposeViewModel(errorRegistry) {

    private val neptunCode = MutableStateFlow<String?>(null)
    private val password = MutableStateFlow<String?>(null)
    private val stayLoggedIn = MutableStateFlow(false)
    private val forcedState = MutableStateFlow<LoginState?>(null)

    val loginState = combine(
        neptunCode,
        password,
        stayLoggedIn,
        forcedState
    ) { neptunCode, password, stayLoggedIn, forcedState ->
        forcedState ?: LoginState.Idle(
            neptunCode = neptunCode.orEmpty(),
            password = password.orEmpty(),
            stayLoggedIn = stayLoggedIn,
            isButtonEnabled = neptunCode.isNullOrEmpty().not() && password.isNullOrEmpty().not()
        )
    }.stateWhileSubscribed(default = null)

    fun login() {
        viewModelScope.launchReportingErrors {
            loginUseCase(
                input = LoginUseCase.Input(
                    neptunCode = neptunCode.value,
                    password = password.value,
                    stayLoggedIn = stayLoggedIn.value,
                )
            ).registerToGeneralErrors { result ->
                when (result) {
                    is LoginUseCase.Result.Error -> result.errorContent
                    else -> null
                }
            }.collect { result ->
                when (result) {
                    is LoginUseCase.Result.Error -> {
                        forcedState.tryEmit(null)
                    }

                    is LoginUseCase.Result.Success -> {
                        forcedState.tryEmit(LoginState.Success(result.studentData))
                    }

                    LoginUseCase.Result.Loading -> {
                        forcedState.tryEmit(LoginState.Loading)
                    }
                }
            }
        }
    }

    fun passwordInput(input: String) {
        password.tryEmit(input)
    }

    fun neptunCodeInput(input: String) {
        neptunCode.tryEmit(input)
    }

    fun keepMeLoggedIn(keep: Boolean) {
        stayLoggedIn.tryEmit(keep)
    }

    fun setIdle() {
        forcedState.tryEmit(null)
    }
}
