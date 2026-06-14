package hu.kocsisgeri.betterneptun.domain.initializable

import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

/**
 * Defines a contract for components that require an asynchronous initialization process.
 *
 * This interface provides a way to trigger initialization and observe its current status
 * through a [StateFlow].
 */
interface Initializer {

    /**
     * A [StateFlow] representing the current state of the initialization process.
     */
    val initializationState: StateFlow<State>

    /**
     * Starts the initialization process.
     */
    fun initialize()

    /**
     * Represents the possible states of the initialization process.
     */
    sealed interface State {

        /**
         * Represents the initial state of the [Initializer] before the [initialize] process
         * has been started.
         */
        data object Idle: State

        /**
         * Represents the state where the initialization process has completed successfully.
         * The components are all ready.
         */
        data object Initialized: State

        /**
         * Indicates that the initialization process is currently in progress.
         */
        data object Initializing: State

        /**
         * Represents a failed initialization state.
         *
         * @property errorContent A description of the error that occurred during the process
         * encapsulated in an [ErrorContent] object.
         */
        data class Error(val errorContent: ErrorContent): State

        val doneLoading: Boolean get() = when (this) {
            is Error,
            Initialized -> true
            else -> false
        }
    }
}
