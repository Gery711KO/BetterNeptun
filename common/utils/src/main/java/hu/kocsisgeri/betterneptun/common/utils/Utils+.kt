package hu.kocsisgeri.betterneptun.common.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

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
