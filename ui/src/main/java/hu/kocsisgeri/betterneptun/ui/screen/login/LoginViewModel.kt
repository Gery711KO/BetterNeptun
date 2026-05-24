package hu.kocsisgeri.betterneptun.ui.screen.login

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.screen.login.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class LoginViewModel(private val loginRepository: LoginRepository) : ComposeViewModel() {

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

    init {
        viewModelScope.launchReportingErrors {
            loginRepository.studentData.collect { result ->
                when (result) {
                    is ApiResult.Error -> forcedState.emit(
                        LoginState.Error(result.error)
                    )

                    is ApiResult.Loading -> {
                        // No nothing
                    }

                    is ApiResult.Success -> forcedState.emit(
                        LoginState.Success(result.data)
                    )
                }
            }
        }
    }

    fun login() {
        viewModelScope.launchReportingErrors {
            val neptunCode = neptunCode.value
            val password = password.value

            if (neptunCode.isNullOrEmpty() || password.isNullOrEmpty()) {
                return@launchReportingErrors
            }

            loginRepository.saveCurrentUser(neptunCode, password)

            forcedState.emit(LoginState.Loading)

            loginRepository.login(neptunCode, password)
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
        loginRepository.saveAutoLoginPreference(keep)
    }

    fun setIdle() {
        forcedState.tryEmit(null)
    }
}