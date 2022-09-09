package hu.kocsisgeri.betterneptun.domain.api.network

import timber.log.Timber
import java.io.IOException

sealed class NetworkResponse<out T : Any, out U : Any> {
    /**
     * Success response with body
     */
    data class Success<T : Any>(val data: T) : NetworkResponse<T, Nothing>()

    data class Failure<U : Any>(
        val error: NetworkErrorResponse<U>
    ) : NetworkResponse<Nothing, Nothing>()
}


sealed class NetworkErrorResponse<out U : Any> {
    abstract fun logError()

    /**
     * Failure response with body
     */
    data class ApiError<U : Any>(val body: U, val code: Int) : NetworkErrorResponse<U>() {
        override fun logError() {
            Timber.e("ApiError - code: $code - $body")
        }
    }

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : NetworkErrorResponse<Nothing>() {
        override fun logError() {
            Timber.e("NetworkError - ${error.message}", error)
        }
    }

    /**
     * Authentication error
     */
    data class AuthenticationError(val code: Int) : NetworkErrorResponse<Nothing>() {
        override fun logError() {
            Timber.e("AuthenticationError - code: $code")
        }
    }

    /**
     * For example, json parsing error
     */
    data class UnknownError(val error: Throwable? = null) : NetworkErrorResponse<Nothing>() {
        override fun logError() {
            Timber.e("UnknownError - ${error?.message}", error)
        }
    }
}