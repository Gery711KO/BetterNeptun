package hu.kocsisgeri.betterneptun.ui.screen.loading

import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.screen.login.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class LoadingViewModel(
    private val initializer: Initializer,
    loginRepository: LoginRepository,
) : ComposeViewModel() {

    private val _nextDestination = MutableStateFlow<NavKey?>(null)

    val initializationState = initializer.isInitialized.stateWhileSubscribed()

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

        viewModelScope.launchReportingErrors {
            loginRepository.studentData.collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        // TODO
                    }

                    is ApiResult.Loading -> {
                        // TODO
                    }

                    is ApiResult.Success -> {
                        _nextDestination.value = HomeDestination
                    }
                }
            }
        }

        viewModelScope.launchReportingErrors {
            loginRepository.shouldAutoLogin.collect { autoLogin ->
                if (autoLogin) {
                    loginRepository.silentLogin()
                } else {
                    _nextDestination.value = LoginDestination
                }
            }
        }
    }

    fun initialize() {
        initializer.initialize(viewModelScope)
    }
}