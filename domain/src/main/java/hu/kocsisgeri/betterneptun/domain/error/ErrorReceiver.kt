package hu.kocsisgeri.betterneptun.domain.error

import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import kotlinx.coroutines.flow.SharedFlow

/**
 * Defines a contract for components that expose a stream of application errors.
 *
 * This interface is used by classes that catch or generate errors and need to
 * broadcast them to observers, such as UI components for displaying error messages.
 *
 * @property errorCallback A [SharedFlow] emitting [ErrorContent] objects representing the errors occurred.
 */
interface ErrorReceiver {

    /**
     * A [SharedFlow] that broadcasts [ErrorContent] events to be handled by observers.
     */
    val errorCallback: SharedFlow<ErrorContent>
}