package hu.kocsisgeri.betterneptun.domain.error.model

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class ErrorEvent(private val content: ErrorContent) {

    @OptIn(ExperimentalAtomicApi::class)
    private val isHandled = AtomicBoolean(false)

    @OptIn(ExperimentalAtomicApi::class)
    fun receiveContent(): ErrorContent? {
        return content.takeIf {
            isHandled.compareAndSet(
                expectedValue = false,
                newValue = true
            )
        }
    }
}
