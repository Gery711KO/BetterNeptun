package hu.kocsisgeri.betterneptun.domain.usecase

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class UseCase {

    private val mutex = Mutex()

    protected suspend fun withLock(block: suspend () -> Unit) {
        if (mutex.isLocked) return

        mutex.withLock { block() }
    }
}
