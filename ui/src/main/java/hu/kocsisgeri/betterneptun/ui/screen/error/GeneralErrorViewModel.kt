package hu.kocsisgeri.betterneptun.ui.screen.error

import androidx.lifecycle.viewModelScope
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.error.ErrorReceiver
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorAction
import hu.kocsisgeri.betterneptun.domain.error.ErrorSender
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.ui.core.ComposeViewModel
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoadingDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class GeneralErrorViewModel(
    private val navigator: Navigator,
    private val errorSender: ErrorSender,
    errorReceiver: ErrorReceiver,
): ComposeViewModel() {

    private val mutex = Mutex()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.stateWhileSubscribed()

    val latestErrorContent = errorReceiver.errorCallback
        .filterIsInstance<ErrorContent.FullScreen>()
        .stateWhileSubscribed(null)

    fun launchAction(errorAction: ErrorAction) {
        if (mutex.isLocked) return

        viewModelScope.launchReportingErrors {
            mutex.withLock {
                when (errorAction) {
                    is ErrorAction.Normal -> errorAction.action.handle()
                    is ErrorAction.Suspend -> {
                        _isLoading.value = true
                        delay(1.seconds)
                        val result = errorAction.action()
                        if (result) {
                            errorAction.onSuccess.handle()
                        }
                    }
                }
            }
        }.invokeOnCompletion {
            _isLoading.value = false
        }
    }

    private fun ErrorAction.PredefinedAction.handle() {
        when (this) {
            ErrorAction.PredefinedAction.NavigateBack -> navigator.navigateBack()
            ErrorAction.PredefinedAction.NavigateToLoading -> navigator.navigateToInclusive(LoadingDestination)
            ErrorAction.PredefinedAction.NavigateBackToHome -> navigator.navigateBack(to = HomeDestination)
            ErrorAction.PredefinedAction.PopThenNavigateBackToDestinationBeforeCurrent -> {
                if (navigator.backStack.size >= 3) {
                    navigator.navigateBack(to = navigator.backStack.takeLast(3).first())
                } else {
                    navigator.navigateBack(to = HomeDestination)
                }
            }
        }
    }
}