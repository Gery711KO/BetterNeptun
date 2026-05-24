package hu.kocsisgeri.betterneptun.domain.usecase.login

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.StudentData
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import kotlinx.coroutines.CoroutineScope

class LoginUseCase(private val loginRepository: LoginRepository) {

    operator fun invoke(
        input: Input,
        onLaunch: (suspend CoroutineScope.() -> Unit) -> Unit,
        onResult: (Result) -> Unit,
    ) {
        onLaunch {
            if (input.neptunCode.isNullOrEmpty() || input.password.isNullOrEmpty()) return@onLaunch

            onResult(Result.Loading)

            loginRepository.saveCurrentUser(
                neptunCode = input.neptunCode,
                password = input.password
            )
            loginRepository.saveAutoLoginPreference(
                shouldAutoLogin = input.stayLoggedIn
            )
            loginRepository.login(
                neptunCode = input.neptunCode,
                password = input.password
            ).collect { result ->
                when (result) {
                    is ApiResult.Loading -> onResult(Result.Loading)
                    is ApiResult.Error -> onResult(Result.Error(result.error))
                    is ApiResult.Success -> onResult(Result.Success(result.data))
                }
            }
        }
    }

    data class Input(
        val neptunCode: String?,
        val password: String?,
        val stayLoggedIn: Boolean,
    )

    sealed interface Result {

        data object Loading: Result
        data class Error(val message: String): Result
        data class Success(val studentData: StudentData): Result
    }
}
