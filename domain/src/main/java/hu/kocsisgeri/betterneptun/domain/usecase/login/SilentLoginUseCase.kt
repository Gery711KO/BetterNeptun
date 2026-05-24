package hu.kocsisgeri.betterneptun.domain.usecase.login

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import kotlinx.coroutines.flow.first

class SilentLoginUseCase(private val loginRepository: LoginRepository): UseCase() {

    suspend operator fun invoke(
        onResult: (Result) -> Unit,
    ) = withLock {
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

    sealed interface Result {

        data object Loading: Result
        data object NavigateToLogin: Result
        data object NavigateToHome: Result
    }
}
