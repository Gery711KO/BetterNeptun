package hu.kocsisgeri.betterneptun.ui.screen.loading

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.domain.usecase.auth.SilentLoginUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class LoadingViewModel(
    private val initializer: Initializer,
    silentLoginUseCase: SilentLoginUseCase,
) : ComposeViewModel() {

    private val silentLoginResult = silentLoginUseCase().map { result ->
        when (result) {
            SilentLoginUseCase.Result.Loading -> null
            SilentLoginUseCase.Result.NavigateToHome -> HomeDestination
            SilentLoginUseCase.Result.NavigateToLogin -> LoginDestination
        }
    }

    val initializationState = initializer.initializationState.stateWhileSubscribed()

    val nextDestination = combine(
        initializationState,
        silentLoginResult,
    ) { initState, nextDestination ->
        when (initState) {
            Initializer.State.Initialized -> nextDestination
            else -> null
        }
    }

    init {
        initialize()
    }

    fun initialize() {
        initializer.initialize(viewModelScope)
    }
}
