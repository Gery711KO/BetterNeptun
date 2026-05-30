package hu.kocsisgeri.betterneptun.domain.initializable

import kotlinx.coroutines.flow.SharedFlow

/**
 * Represents a component that requires an asynchronous initialization process
 */
interface Initializable {

    /**
     * A [SharedFlow] that emits the current initialization state of the component.
     */
    val isInitialized: SharedFlow<Boolean>

    /**
     * Starts the initialization process for the component.
     * This is a suspending function that performs the necessary setup,
     * such as data loading or configuration, required for the component to function.
     */
    suspend fun initialize()
}
