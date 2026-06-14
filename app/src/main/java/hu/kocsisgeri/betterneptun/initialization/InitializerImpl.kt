package hu.kocsisgeri.betterneptun.initialization

import dev.jordond.connectivity.Connectivity
import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorAction
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.error.fullScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Singleton

@Singleton
internal class InitializerImpl(
    private val scope: CoroutineScope,
    private val initializables: List<Initializable>
) : Initializer {

    private val connectivity = Connectivity()

    private val innerState =
        MutableStateFlow<Initializer.State>(Initializer.State.Idle)

    override val initializationState = innerState.asStateFlow()

    override fun initialize() {
        if (innerState.value !is Initializer.State.Initializing) {
            innerState.value = Initializer.State.Initializing

            connectivity.start()

            scope.launchReportingErrors(
                handleError = {
                    innerState.value = Initializer.State.Error(
                        ErrorContent.fullScreen(
                            title = "Initialization failed",
                            description = "Something went wrong please try again.",
                            primaryAction = retryAction(),
                            inclusive = true,
                        )
                    )
                }
            ) {
                when (connectivity.status()) {
                    is Connectivity.Status.Connected -> {
                        innerState.value = initializables
                            .map { async { it.initialize() } }
                            .awaitAll()
                            .all { it }
                            .let { isInitialized ->
                                if (isInitialized) Initializer.State.Initialized
                                else Initializer.State.Error(
                                    ErrorContent.fullScreen(
                                        title = "Server error",
                                        description = "Servers may be down, please try again later.",
                                        primaryAction = retryAction(),
                                        inclusive = true,
                                    )
                                )
                            }
                    }
                    Connectivity.Status.Disconnected -> {
                        innerState.value = Initializer.State.Error(
                            ErrorContent.FullScreen(
                                icon = R.raw.network_error_lottie,
                                title = "Connection error",
                                description = "You have no internet connection, please check your network connection and try again.",
                                primaryAction = retryAction(),
                                inclusive = true,
                            )
                        )
                    }
                }
            }.invokeOnCompletion {
                connectivity.stop()
            }
        }
    }

    private fun retryAction() = ErrorAction.Suspend(
        label = "Retry",
        onSuccess = ErrorAction.PredefinedAction.NavigateToLoading,
        action = {
            initialize()
            val result = initializationState.first { it.doneLoading }

            result is Initializer.State.Initialized
        }
    )
}
