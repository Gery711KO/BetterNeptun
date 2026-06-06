package hu.kocsisgeri.betterneptun.initialization

import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Singleton

@Singleton
internal class InitializerImpl(private val initializables: List<Initializable>): Initializer {

    private val innerState =
        MutableStateFlow<Initializer.State>(Initializer.State.Idle)

    override val initializationState = innerState.asStateFlow()

    override fun initialize(scope: CoroutineScope) {
        if (innerState.value !is Initializer.State.Initializing) {
            innerState.value = Initializer.State.Initializing

            scope.launchReportingErrors(
                handleError = {
                    innerState.value = Initializer.State.Error(
                        errorMessage = it.message?: "Initialization failed."
                    )
                }
            ) {
                innerState.value = initializables
                    .map { async { it.initialize() } }
                    .awaitAll()
                    .all { it }
                    .let {
                        if (it) Initializer.State.Initialized
                        else Initializer.State.Error("Server error\nPlease try again later.")
                    }
            }
        }
    }
}
