package hu.kocsisgeri.betterneptun.data.util

import hu.kocsisgeri.betterneptun.domain.model.neptun.ApiResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
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
        }
    }
}