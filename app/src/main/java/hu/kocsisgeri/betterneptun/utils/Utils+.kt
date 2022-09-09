package hu.kocsisgeri.betterneptun.utils

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import hu.kocsisgeri.betterneptun.R
import io.noties.markwon.Markwon
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

enum class ThemeMode(val mode: Int) {
    AUTO(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM), DARK(AppCompatDelegate.MODE_NIGHT_YES), LIGHT(AppCompatDelegate.MODE_NIGHT_NO)
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

fun TextView.setMarkdownText(text: String?) {
    val markwon = Markwon.create(context)
    val newText = text?.let { markwon.toMarkdown(text) }
    this.text = if (newText?.contains("}") == true) {
        newText.split("}").last()
    } else {
        newText
    }
}

fun TextView.setHtmlText(
    text: String?
) {
    this.text = if (text?.contains("}") == true) {
        Html.fromHtml(text.split("}").last(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    }
}
fun TextView.setTextAndAddClickableLinks(
    markdown: String?,
    context: Context,
    navController: NavController
) {
    setHtmlText(markdown)
    //setMarkdownText(markdown)
    handleUrlClicks { text ->
        when {
            text.contains("http") -> {
                navController.popBackStack()
                openUrl(text, context)
            }
            text.contains("mailto:") || text.contains("@") -> {
                text.removePrefix("mailto:").trim().sendEmail(context)
            }
            else -> {}
        }
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

fun TextView.handleUrlClicks(onClicked: ((String) -> Unit)? = null) {
    //create span builder and replaces current text with it
    text = SpannableStringBuilder.valueOf(text).apply {
        //search for all URL spans and replace all spans with our own clickable spans
        getSpans(0, length, URLSpan::class.java).forEach {
            //add new clickable span at the same position
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onClicked?.invoke(it.url)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = context.getColor(R.color.radio_button_color)
                        ds.bgColor = Color.TRANSPARENT
                    }
                },
                getSpanStart(it),
                getSpanEnd(it),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
            )
            //remove old URLSpan
            removeSpan(it)
        }
    }
    //make sure movement method is set
    movementMethod = LinkMovementMethod.getInstance()
}

fun Context.isUsingNightMode(): Boolean {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
        else -> false
    }
}

fun Context.getCurrentTheme(): ThemeMode {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> ThemeMode.DARK
        Configuration.UI_MODE_NIGHT_NO -> ThemeMode.LIGHT
        else -> ThemeMode.AUTO
    }
}

fun LocalDateTime.getCourseDateString(): String {
    val diff =
        this.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val days = TimeUnit.MILLISECONDS.toDays(diff * 1000)
    val hours = TimeUnit.MILLISECONDS.toHours(diff * 1000)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff * 1000)
    return when {
        minutes < 60 -> "$minutes perc múlva"
        hours < 24 -> "$hours óra múlva"
        days in 1..1 -> "Holnap"
        days >= 2 -> "$days nap múlva"
        else -> "$days nap múlva"
    }
}

fun Fragment.setBackButton(view: View) {
    view.setOnClickListener {
        findNavController().popBackStack()
    }
}

fun Fragment.setButtonNavigation(view: View, destination: NavDirections) {
    view.setOnClickListener {
        findNavController().navigate(destination)
    }
}

fun Fragment.showToastOnClick(view: View, text: String) {
    view.setOnClickListener {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}