package hu.kocsisgeri.betterneptun.ui.error

import hu.kocsisgeri.betterneptun.domain.error.ErrorReceiver
import hu.kocsisgeri.betterneptun.domain.error.ErrorRegistry
import hu.kocsisgeri.betterneptun.domain.error.ErrorSender
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Singleton

@Singleton
class ErrorRegistryImpl: ErrorRegistry, ErrorSender, ErrorReceiver {

    override val errorCallback =
        MutableSharedFlow<ErrorContent>(
            replay = 10,
            extraBufferCapacity = 10
        )

    override fun <T> Flow<T>.registerToGeneralErrors(
        contentResolver: suspend (T) -> ErrorContent?
    ): Flow<T> = onEach { value ->
        contentResolver(value)?.let { error ->
            send(error)
        }
    }

    override fun send(error: ErrorContent) {
        errorCallback.tryEmit(error)
    }
}
