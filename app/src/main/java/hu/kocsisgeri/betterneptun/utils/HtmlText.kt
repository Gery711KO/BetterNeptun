package hu.kocsisgeri.betterneptun.utils

import android.text.Html
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import hu.kocsisgeri.betterneptun.ui.theme.Armata

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontFamily: FontFamily = Armata,
    onUrlClick: (String) -> Unit = {}
) {
    val spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    val annotatedString = spanned.toAnnotatedString()

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = fontSize,
            fontFamily = fontFamily
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onUrlClick(annotation.item)
                }
        }
    )
}

fun Spanned.toAnnotatedString(): AnnotatedString {
    val builder = AnnotatedString.Builder(this.toString())
    val spans = getSpans(0, length, Any::class.java)

    spans.forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        when (span) {
            is android.text.style.StyleSpan -> {
                when (span.style) {
                    android.graphics.Typeface.BOLD -> builder.addStyle(SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), start, end)
                    android.graphics.Typeface.ITALIC -> builder.addStyle(SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), start, end)
                    android.graphics.Typeface.BOLD_ITALIC -> builder.addStyle(SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), start, end)
                }
            }
            is android.text.style.UnderlineSpan -> builder.addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
            is URLSpan -> {
                builder.addStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline), start, end)
                builder.addStringAnnotation(tag = "URL", annotation = span.url, start = start, end = end)
            }
        }
    }
    return builder.toAnnotatedString()
}
