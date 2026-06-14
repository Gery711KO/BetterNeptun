package hu.kocsisgeri.betterneptun.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

/**
 * Base class for domain-level use cases providing synchronization mechanisms to prevent concurrent execution.
 *
 * This class utilizes a [Mutex] to ensure that protected blocks or flows within a specific use case
 * instance do not run simultaneously. If a process is already active, subsequent attempts to execute
 * the locked logic will be skipped and logged as a warning.
 */
abstract class UseCase {

    private val mutex = Mutex()

    /**
     * Executes the given [block] within a mutual exclusion lock.
     *
     * If the lock is already held by another process, the execution is skipped, and a warning
     * is logged. This ensures that the protected action is not executed concurrently.
     *
     * @param block The suspendable action to be performed under the lock.
     */
    protected suspend fun withLock(block: suspend () -> Unit) {
        if (mutex.isLocked) {
            Timber.tag("LOCKED").w("This action is currently locked.")
            return
        }

        mutex.withLock { block() }
    }

    /**
     * Creates a synchronized [Flow].
     *
     * If the mutex is already locked when the flow is collected, the execution is
     * aborted, a warning is logged, and the flow completes immediately without
     * emitting any items. Otherwise, it executes the provided [block] within the
     * lock to ensure that only one instance of this use case logic runs at a time.
     *
     * @param T The type of data emitted by the flow.
     * @param block The flow builder block to be executed under the lock.
     */
    protected fun <T> lockedFlow(block: suspend FlowCollector<T>.() -> Unit) = flow {
        if (mutex.isLocked) {
            Timber.tag("LOCKED").w("This action is currently locked.")
            return@flow
        }

        mutex.withLock { block() }
    }

    /**
     * Executes the given [block] exclusively. If the lock is already held by another
     * execution, this method suspends until the lock is released before the next in line
     * is executed.
     *
     * If this function is called 3 times in the following order A -> B -> C.
     * A waiting line is formed it will be executed in a First In First Out order.
     *
     * @param block The suspending action to be performed after the lock becomes available.
     */
    protected suspend fun withSuspendingLock(block: suspend () -> Unit) {
        if (mutex.isLocked) {
            Timber
                .tag("LOCKED")
                .w("This action is currently locked. Suspending until completion.")
        }
        mutex.withLock { block() }
    }

    /**
     * Executes the given [block] exclusively. If the lock is already held by another
     * execution, this method suspends until the lock is released before the next in line
     * is executed.
     *
     * If this function is called 3 times in the following order A -> B -> C.
     * A waiting line is formed it will be executed in a First In First Out order.
     *
     * @param block The suspending action to be performed after the lock becomes available.
     * @return The result from the [Mutex] withLock body as [T].
     */
    protected suspend fun <T> withReturningLock(block: suspend () -> T): T {
        if (mutex.isLocked) {
            Timber
                .tag("LOCKED")
                .w("This action is currently locked. Suspending until completion.")
        }
        return mutex.withLock { block() }
    }
}
