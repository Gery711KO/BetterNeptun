package hu.kocsisgeri.betterneptun.domain.usecase.login

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first

class SilentLoginUseCase(private val loginRepository: LoginRepository) {

    operator fun invoke(
        onLaunch: (suspend CoroutineScope.() -> Unit) -> Unit,
        onResult: (Result) -> Unit,
    ) {
        onLaunch {
            onResult(Result.Loading)
            loginRepository.shouldAutoLogin.first().let { autoLogin ->
                if (autoLogin) {
                    loginRepository.silentLogin().collect { result ->
                        when (result) {
                            is ApiResult.Loading -> onResult(Result.Loading)
                            is ApiResult.Error -> onResult(Result.NavigateToLogin)
                            is ApiResult.Success -> onResult(Result.NavigateToHome)
                        }
                    }
                } else {
                    onResult(Result.NavigateToLogin)
                }
            }
        }
    }

    sealed interface Result {

        data object Loading: Result
        data object NavigateToLogin: Result
        data object NavigateToHome: Result
    }
}
