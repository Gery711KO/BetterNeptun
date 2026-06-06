package hu.kocsisgeri.betterneptun.common.utils

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

fun CoroutineScope.launchReportingErrors(
    handleError: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
) = launch(
    context = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
        handleError(throwable)
    },
    block = block
)
