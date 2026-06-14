package hu.kocsisgeri.betterneptun.domain.error

import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing and centralizing the registration of domain-level errors.
 *
 * Provides a mechanism to intercept data streams and report specific conditions
 * to a global error handling system.
 */
interface ErrorRegistry {

    /**
     * Registers items from the [Flow] to a general error handling system.
     *
     * This extension function monitors the flow and uses the provided [contentResolver]
     * to determine if an emitted item should be treated as an error. If the resolver
     * returns a non-null [ErrorContent], the error is registered within the registry.
     *
     * @param T The type of the elements in the flow.
     * @param contentResolver A suspending function that maps a flow item to an [ErrorContent]
     * if it represents an error, or null otherwise.
     * @return The original [Flow] for further transformation or collection.
     */
    fun <T> Flow<T>.registerToGeneralErrors(
        contentResolver: suspend (T) -> ErrorContent?
    ): Flow<T>
}