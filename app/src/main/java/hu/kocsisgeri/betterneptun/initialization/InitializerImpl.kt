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

class InitializerImpl(private val initializables: List<Initializable>): Initializer {
    private val _isInitialized = MutableStateFlow(false)
    override val isInitialized = _isInitialized.asStateFlow()

    override fun initialize(scope: CoroutineScope) {
        scope.launchReportingErrors {
            _isInitialized.value = initializables
                .map { initializable ->
                    async {
                        initializable.initialize()
                        initializable.isInitialized.first { it }
                    }
                }
                .awaitAll()
                .all { it }
        }
    }
}