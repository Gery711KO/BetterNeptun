package hu.kocsisgeri.betterneptun.initialization

import hu.kocsisgeri.betterneptun.common.utils.launchReportingErrors
import hu.kocsisgeri.betterneptun.domain.initializable.Initializable
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

internal class InitializerImpl(private val initializables: List<Initializable>): Initializer {

    private val _isInitialized =
        MutableStateFlow<Initializer.State>(Initializer.State.Idle)

    override val initializationState = _isInitialized.asStateFlow()

    override fun initialize(scope: CoroutineScope) {
        if (_isInitialized.value !is Initializer.State.Initializing) {
            _isInitialized.value = Initializer.State.Initializing

            scope.launchReportingErrors(
                handleError = {
                    _isInitialized.value = Initializer.State.Error(
                        errorMessage = it.message?: "Initialization failed."
                    )
                }
            ) {
                _isInitialized.value = initializables
                    .map { initializable ->
                        async {
                            initializable.initialize()
                            initializable.isInitialized.first()
                        }
                    }
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