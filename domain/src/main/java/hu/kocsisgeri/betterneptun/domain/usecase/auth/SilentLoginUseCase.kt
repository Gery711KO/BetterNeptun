package hu.kocsisgeri.betterneptun.domain.usecase.auth

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class SilentLoginUseCase(
    private val loginRepository: LoginRepository,
): UseCase() {

    operator fun invoke() = lockedFlow {
        emit(Result.Loading)
        loginRepository.shouldAutoLogin.first().let { autoLogin ->
            if (autoLogin) {
                loginRepository.silentLogin().collect { result ->
                    when (result) {
                        is ApiResult.Loading -> emit(Result.Loading)
                        is ApiResult.Error -> emit(Result.NavigateToLogin)
                        is ApiResult.Success -> emit(Result.NavigateToHome)
                    }
                }
            } else {
                emit(Result.NavigateToLogin)
            }
        }
    }

    sealed interface Result {

        data object Loading: Result
        data object NavigateToLogin: Result
        data object NavigateToHome: Result
    }
}
