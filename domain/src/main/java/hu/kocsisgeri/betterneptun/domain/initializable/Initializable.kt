package hu.kocsisgeri.betterneptun.domain.initializable

/**
 * Represents a component that requires an asynchronous initialization process
 */
interface Initializable {


    /**
     * Starts the initialization process for the component.
     * This is a suspending function that performs the necessary setup,
     * such as data loading or configuration, required for the component to function.
     */
    suspend fun initialize(): Boolean
}
