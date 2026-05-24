package hu.kocsisgeri.betterneptun.ui.screen.loading

import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.domain.usecase.login.SilentLoginUseCase
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.destination.LoginDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class LoadingViewModel(
    private val initializer: Initializer,
    private val silentLoginUseCase: SilentLoginUseCase,
) : ComposeViewModel() {

    private val _nextDestination = MutableStateFlow<NavKey?>(null)

    val initializationState = initializer.initializationState.stateWhileSubscribed()

    val nextDestination = combine(
        initializationState,
        _nextDestination
    ) { initState, nextDestination ->
        when (initState) {
            Initializer.State.Initialized -> nextDestination
            else -> null
        }
    }

    init {
        initialize()
        handleSilentLogin()
    }

    fun initialize() {
        initializer.initialize(viewModelScope)
    }

    private fun handleSilentLogin() {
        viewModelScope.launchReportingErrors {
            silentLoginUseCase { result ->
                when (result) {
                    SilentLoginUseCase.Result.Loading -> Unit
                    SilentLoginUseCase.Result.NavigateToHome -> {
                        _nextDestination.value = HomeDestination
                    }

                    SilentLoginUseCase.Result.NavigateToLogin -> {
                        _nextDestination.value = LoginDestination
                    }
                }
            }
        }
    }
}
