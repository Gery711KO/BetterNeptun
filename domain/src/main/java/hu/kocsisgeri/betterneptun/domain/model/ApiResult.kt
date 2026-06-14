package hu.kocsisgeri.betterneptun.domain.model

sealed class ApiResult<out T : Any> {
    data object Loading : ApiResult<Nothing>()
    data class Success<out T : Any>(val data: T) : ApiResult<T>()
    data class Error(val error: String) : ApiResult<Nothing>()
}

interface UiResult<out T : Any> {
    data object Loading : UiResult<Nothing>
    data class Success<out T : Any>(val data: T) : UiResult<T>
}

fun <T: Any> UiResult<T>.onSuccess(
    data: (T) -> Unit,
) = this.also {
    when(this) {
        is UiResult.Success -> data(this.data)
    }
}

fun <T: Any> UiResult<T>.onLoading(
    loading: () -> Unit,
) = this.also {
    when(this) {
        is UiResult.Loading -> loading()
    }
}

fun <T: Any> ApiResult<T>.mapToUiResult() = when (this) {
    is ApiResult.Error,
    ApiResult.Loading -> UiResult.Loading
    is ApiResult.Success -> UiResult.Success(data)
}
