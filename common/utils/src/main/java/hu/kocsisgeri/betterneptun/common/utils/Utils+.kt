package hu.kocsisgeri.betterneptun.common.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.roundToInt
import androidx.core.net.toUri

fun openUrl(url: String?, context: Context) {
    url?.let {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data =
            if (url.startsWith("http://") || url.startsWith("https://")) {
                it.toUri()
            } else {
                "http://${it}".toUri()
            }
        context.startActivity(openURL, null)
    }
}

fun String.sendEmail(context: Context) {
    val selectorIntent = Intent(Intent.ACTION_SENDTO)
    selectorIntent.data = "mailto:".toUri()
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(this))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
    emailIntent.putExtra(Intent.EXTRA_TEXT, "")
    emailIntent.selector = selectorIntent
    context.startActivity(
        Intent.createChooser(emailIntent, "Send email..."),
        Bundle.EMPTY
    )
}

fun LocalDateTime.getCourseDateString(): String {
    val diff =
        this.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val hours = TimeUnit.MILLISECONDS.toHours(diff * 1000)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    val minutes = ceil(seconds / 60f).roundToInt()
    val days = hours / 24f
    return when {
        minutes < 60 -> "$minutes perc múlva"
        hours <= hour -> "$hours óra múlva"
        days < 1 -> "Holnap"
        days > 1 -> "${ceil(days).roundToInt()} nap múlva"
        else -> "${ceil(days).roundToInt()} nap múlva"
    }
}

fun LocalDateTime.getTimeLeft(): String {
    val diff =
        this.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    val minutes = ceil(seconds / 60f).roundToInt()
    return "$minutes perc"
}

fun CoroutineScope.launchReportingErrors(
    handleError: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
) {
    launch(
        context = CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable)
            handleError(throwable)
        },
        block = block
    )
}
