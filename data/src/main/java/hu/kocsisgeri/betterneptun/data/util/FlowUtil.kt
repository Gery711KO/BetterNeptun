package hu.kocsisgeri.betterneptun.data.util

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

suspend fun <T : Any> MutableStateFlow<ApiResult<T>>.runApiCall(
    dispatcher: CoroutineDispatcher,
    block: suspend () -> T
) {
    withContext(dispatcher) {
        try {
            value = ApiResult.Loading
            value = ApiResult.Success(block())
        } catch (exception: HttpException) {
            value = ApiResult.Error(exception.message ?: "Network error.")
        } catch (exception: IOException) {
            value = ApiResult.Error(exception.message ?: "Something went wrong.")
        } catch (exception: SerializationException) {
            value = ApiResult.Error(exception.message ?: "Serialization error.")
        } catch (exception: CancellationException) {
            value = ApiResult.Error(exception.message ?: "Operation canceled.")
        }
    }
}

suspend fun <T : Any> MutableSharedFlow<ApiResult<T>>.runApiCall(
    dispatcher: CoroutineDispatcher,
    block: suspend () -> T
) {
    withContext(dispatcher) {
        try {
            emit(ApiResult.Loading)
            emit(ApiResult.Success(block()))
        } catch (exception: HttpException) {
            emit(ApiResult.Error(exception.message ?: "Network error."))
        } catch (exception: IOException) {
            emit(ApiResult.Error(exception.message ?: "Something went wrong."))
        } catch (exception: SerializationException) {
            emit(ApiResult.Error(exception.message ?: "Serialization error."))
        } catch (exception: CancellationException) {
            emit(ApiResult.Error(exception.message ?: "Operation canceled."))
        }
    }
}

fun <T : Any> runApiCall(
    onResult: (T?) -> Unit,
    block: suspend () -> T,
) = callbackFlow {
    try {
        send(ApiResult.Loading)
        val result = block()
        onResult(result)
        send(ApiResult.Success(result))
    } catch (exception: HttpException) {
        send(ApiResult.Error(exception.message ?: "Network error."))
    } catch (exception: IOException) {
        send(ApiResult.Error(exception.message ?: "Something went wrong."))
    } catch (exception: SerializationException) {
        send(ApiResult.Error(exception.message ?: "Serialization error."))
    } catch (exception: CancellationException) {
        send(ApiResult.Error(exception.message ?: "Operation canceled."))
    }

    awaitClose()
}
