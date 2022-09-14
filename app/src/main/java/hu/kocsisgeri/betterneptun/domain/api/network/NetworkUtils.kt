package hu.kocsisgeri.betterneptun.domain.api.network

import timber.log.Timber

inline fun <reified T: Any>NetworkResponse<T, String>.check(onSuccess: (T) -> Unit) {
    when (this) {
        is NetworkResponse.Failure<*> -> {
            Timber.e("${this::class.java.simpleName} - Error occurred")
        }
        is NetworkResponse.Success -> {
            onSuccess(this.data)
        }
    }
}