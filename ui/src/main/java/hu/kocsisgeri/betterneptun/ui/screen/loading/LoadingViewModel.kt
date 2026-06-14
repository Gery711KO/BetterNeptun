package hu.kocsisgeri.betterneptun.ui.screen.loading

import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.domain.error.ErrorRegistry
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.domain.usecase.auth.LogoutUseCase
import hu.kocsisgeri.betterneptun.domain.usecase.auth.SilentLoginUseCase
import hu.kocsisgeri.betterneptun.ui.core.ErrorHandlingComposeViewModel
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class LoadingViewModel(
    private val initializer: Initializer,
    private val logoutUseCase: LogoutUseCase,
    silentLoginUseCase: SilentLoginUseCase,
    errorRegistry: ErrorRegistry,
) : ErrorHandlingComposeViewModel(errorRegistry) {

    val initializationState = initializer.initializationState.stateWhileSubscribed()

    val nextDestination: Flow<NavKey?> = initializationState
        .registerToGeneralErrors {
            when (it) {
                is Initializer.State.Error -> it.errorContent
                else -> null
            }
        }
        .flatMapLatest {
            when (it) {
                Initializer.State.Initialized -> silentLoginUseCase()
                    .map { result ->
                        when (result) {
                            SilentLoginUseCase.Result.Loading -> null
                            SilentLoginUseCase.Result.NavigateToHome -> HomeDestination
                            SilentLoginUseCase.Result.NavigateToLogin -> LoginDestination
                            SilentLoginUseCase.Result.NavigateToLoginWithError -> {
                                logoutUseCase()
                                null
                            }
                        }
                    }

                else -> flowOf(null)
            }
        }.stateWhileSubscribed(null)

    init {
        initialize()
    }

    fun initialize() {
        initializer.initialize()
    }
}
