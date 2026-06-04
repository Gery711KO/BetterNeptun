package hu.kocsisgeri.betterneptun.domain.usecase.auth

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.model.neptun.StudentData
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.usecase.UseCase
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class LoginUseCase(private val loginRepository: LoginRepository): UseCase() {

    operator fun invoke(input: Input) = lockedFlow {
        if (input.neptunCode.isNullOrEmpty() || input.password.isNullOrEmpty()) {
            emit(Result.Error("No credentials found."))
        } else {
            emit(Result.Loading)

            loginRepository.saveCurrentUser(
                neptunCode = input.neptunCode,
                password = input.password
            )
            loginRepository.saveAutoLoginPreference(
                shouldAutoLogin = input.stayLoggedIn
            )

            emitAll(
                loginRepository.login(
                    neptunCode = input.neptunCode,
                    password = input.password
                ).map { result ->
                    when (result) {
                        is ApiResult.Loading -> Result.Loading
                        is ApiResult.Error -> Result.Error(result.error)
                        is ApiResult.Success -> Result.Success(result.data)
                    }
                }
            )
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
