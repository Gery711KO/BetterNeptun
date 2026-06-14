package hu.kocsisgeri.betterneptun.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)

// App primary colors - fully gray theme
val LightDarkColor = Color(0xFF212121)
val MediumDarkColor = Color(0xFF424242)
val FullDarkColor = Color(0xFF121212)
val MainTextColor = Color(0xFF9E9E9E)
val TitleColor = Color(0xFFEEEEEE)

// Light Theme Colors - Gray focused
val LightPrimary = Color(0xFF424242)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFF0F0F0)
val LightOnPrimaryContainer = Color(0xFF212121)

val LightSecondary = Color(0xFF616161)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFFAFAFA)
val LightOnSecondaryContainer = Color(0xFF212121)

val LightTertiary = Color(0xFF757575)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFF5F5F5)
val LightOnTertiaryContainer = Color(0xFF212121)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

val LightBackground = Color(0xFFE0E0E0)
val LightOnBackground = Color(0xFF212121)
val LightSurface = Color(0xFFF5F5F5)
val LightOnSurface = Color(0xFF212121)
val LightSurfaceVariant = Color(0xFFFFFFFF)
val LightOnSurfaceVariant = Color(0xFF424242)
val LightOutline = Color(0xFF757575)

// Dark Theme Colors - Fully Dark Gray
val DarkPrimary = Color(0xFFE0E0E0)
val DarkOnPrimary = Color(0xFF212121)
val DarkPrimaryContainer = Color(0xFF1E1E1E)
val DarkOnPrimaryContainer = Color(0xFFEEEEEE)

val DarkSecondary = Color(0xFFBDBDBD)
val DarkOnSecondary = Color(0xFF212121)
val DarkSecondaryContainer = Color(0xFF282828)
val DarkOnSecondaryContainer = Color(0xFFEEEEEE)

val DarkTertiary = Color(0xFF9E9E9E)
val DarkOnTertiary = Color(0xFF212121)
val DarkTertiaryContainer = Color(0xFF323232)
val DarkOnTertiaryContainer = Color(0xFFEEEEEE)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

val DarkBackground = Color(0xFF0F0F0F) // Even darker background
val DarkOnBackground = Color(0xFFEEEEEE)
val DarkSurface = Color(0xFF0F0F0F)
val DarkOnSurface = Color(0xFFEEEEEE)
val DarkSurfaceVariant = Color(0xFF1E1E1E)
val DarkOnSurfaceVariant = Color(0xFFBDBDBD)
val DarkOutline = Color(0xFF757575)

@Composable
fun themeBasedColor(
    lightColor: Color,
    darkColor: Color,
): Color {
    val isDarkTheme = isSystemInDarkTheme()

    return remember(isDarkTheme) {
        if (isDarkTheme) darkColor
        else lightColor
    }
}

fun isColorDark(color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) < 0.5;
}
