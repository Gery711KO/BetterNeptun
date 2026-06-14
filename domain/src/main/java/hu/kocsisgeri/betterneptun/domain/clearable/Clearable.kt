package hu.kocsisgeri.betterneptun.domain.clearable

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Interface representing a component that can be cleared or reset to its initial state.
 *
 */
interface Clearable {

    /**
     * Clears the state and all registered clearable resources of the object.
     *
     * @param scope The [CoroutineScope] in which the clearing operations will be performed.
     */
    suspend fun clear(scope: CoroutineScope)
}

/**
 * A base implementation of the [Clearable] interface that provides utility methods for managing
 * and resetting stateful components like [MutableStateFlow] and [MutableSharedFlow].
 *
 * This class ensures that all registered clearable properties are reset to their initial
 * state when the [clear] method is invoked. It uses a [Mutex] to prevent concurrent
 * clearing operations and performs the reset logic asynchronously.
 */
abstract class BaseClearable: Clearable {

    private val mutex = Mutex()

    private val clearables = ConcurrentHashMap<String, suspend () -> Unit>()

    /**
     * Creates a [MutableStateFlow] and registers it to be automatically reset to its
     * initial [value] when the [clear] method is called.
     *
     * @param T The type of the value held by the flow.
     * @param value The initial value of the flow, which will also be the value restored upon clearing.
     * @return A [MutableStateFlow] instance managed by this [BaseClearable].
     */
    protected fun <T> clearableStateFlow(value: T) = MutableStateFlow(value).apply {
        clearables[UUID.randomUUID().toString()] = {
            this.value = value
        }
    }

    /**
     * Creates a [MutableSharedFlow] that is automatically registered for clearing.
     * When the [clear] method is called on this [BaseClearable] instance, the
     * shared flow's replay cache will be reset.
     *
     * @param T The type of the flow's elements.
     * @param replay The number of values replayed to new subscribers.
     * @param extraBufferCapacity The number of values buffered in addition to [replay].
     * @param onBufferOverflow Configures an emit action on buffer overflow.
     * @return A [MutableSharedFlow] instance that resets its replay cache upon clearing.
     */
    protected fun <T> clearableSharedFlow(
        replay: Int = 0,
        extraBufferCapacity: Int = 0,
        onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
    ) = MutableSharedFlow<T>(
        replay = replay,
        extraBufferCapacity = extraBufferCapacity,
        onBufferOverflow = onBufferOverflow
    ).apply {
        clearables[UUID.randomUUID().toString()] = {
            resetReplayCache()
        }
    }

    /**
     * Hook method called during the [clear] process.
     *
     * Subclasses can override this method to perform additional cleanup logic
     * after the registered clearable states and flows have been reset.
     */
    open suspend fun onClear() = Unit

    /**
     * Clears the state and resources of the component.
     *
     * @param scope The [CoroutineScope] in which the clearing operations will be executed.
     */
    override suspend fun clear(scope: CoroutineScope) {
        if (mutex.isLocked) return

        mutex.withLock {
            scope.launch {
                clearables.map { (_, clear) ->
                    async { clear() }
                }.awaitAll()

                onClear()
            }
        }
    }
}
