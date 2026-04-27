package hu.kocsisgeri.betterneptun.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import hu.kocsisgeri.betterneptun.ui.screen.timetable.model.CalendarEntity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

enum class ThemeMode(val mode: Int) {
    AUTO(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM), DARK(AppCompatDelegate.MODE_NIGHT_YES), LIGHT(
        AppCompatDelegate.MODE_NIGHT_NO
    )
}

fun openUrl(url: String?, context: Context) {
    url?.let {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data =
            if (url.startsWith("http://") || url.startsWith("https://")) Uri.parse(it) else Uri.parse(
                "http://${it}"
            )
        ContextCompat.startActivity(context, openURL, null)
    }
}

fun String.sendEmail(context: Context) {
    val selectorIntent = Intent(Intent.ACTION_SENDTO)
    selectorIntent.data = Uri.parse("mailto:")
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(this))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
    emailIntent.putExtra(Intent.EXTRA_TEXT, "")
    emailIntent.selector = selectorIntent
    ContextCompat.startActivity(
        context,
        Intent.createChooser(emailIntent, "Send email..."),
        Bundle.EMPTY
    )
}

fun LocalDateTime.getCourseDateString(): String {
    val diff =
        this.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val hours = TimeUnit.MILLISECONDS.toHours(diff * 1000)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    val minutes = kotlin.math.ceil(seconds / 60f).roundToInt()
    val days = hours / 24f
    return when {
        minutes < 60 -> "$minutes perc múlva"
        hours <= hour -> "$hours óra múlva"
        days < 1 -> "Holnap"
        days > 1 -> "${kotlin.math.ceil(days).roundToInt()} nap múlva"
        else -> "${kotlin.math.ceil(days).roundToInt()} nap múlva"
    }
}

fun LocalDateTime.getTimeLeft(): String {
    val diff =
        this.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    val minutes = kotlin.math.ceil(seconds / 60f).roundToInt()
    return "$minutes perc"
}

fun CalendarEntity.Event.getRemainingTime(): Float {
    val diff =
        endTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    return seconds / 60f
}

fun CalendarEntity.Event.getTime(): Float {
    val diff = endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff * 1000)
    return seconds / 60f
}

fun CalendarEntity.Event.getPercent(): Int {
    return (100f - (getRemainingTime() / getTime()) * 100f).roundToInt()
}

fun CoroutineScope.launchReportingErrors(
    block: suspend CoroutineScope.() -> Unit
) {
    launch(
        context = CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable)
        },
        block = block
    )
}
