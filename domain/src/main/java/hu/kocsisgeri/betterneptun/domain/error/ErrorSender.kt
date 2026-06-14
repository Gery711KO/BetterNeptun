package hu.kocsisgeri.betterneptun.domain.error

import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent

/**
 * Defines a contract for components responsible for reporting or transmitting error information.
 */
interface ErrorSender {

    /**
     * Sends the specified error content to the designated error reporting or handling system.
     *
     * @param error the content and metadata of the error to be sent.
     */
    fun send(error: ErrorContent)
}
