package hu.kocsisgeri.betterneptun.common.utils

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
