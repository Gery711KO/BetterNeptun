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

interface Clearable {

    suspend fun clear(scope: CoroutineScope)
}

abstract class BaseClearable: Clearable {

    private val mutex = Mutex()

    private val clearables = ConcurrentHashMap<String, suspend () -> Unit>()

    protected fun <T> clearableStateFlow(value: T) = MutableStateFlow(value).apply {
        clearables[UUID.randomUUID().toString()] = {
            this.value = value
        }
    }

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

    open suspend fun onClear() = Unit

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
