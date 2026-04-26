package hu.kocsisgeri.betterneptun.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkBaseTextColor,
    secondary = LightDarkColor,
    tertiary = DarkRadioButtonColor,
    background = DarkBaseFragmentBg,
    surface = DarkCardBg,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = White,
    onBackground = DarkBaseTextColor,
    onSurface = DarkBaseTextColor,
    outline = DarkTextInputBoxColor
)

private val LightColorScheme = lightColorScheme(
    primary = LightBaseTextColor,
    secondary = LightDarkColor,
    tertiary = LightRadioButtonColor,
    background = LightBaseFragmentBg,
    surface = LightCardBg,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = LightBaseTextColor,
    onSurface = LightBaseTextColor,
    outline = LightTextInputBoxColor
)

@Composable
fun BetterNeptunTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
