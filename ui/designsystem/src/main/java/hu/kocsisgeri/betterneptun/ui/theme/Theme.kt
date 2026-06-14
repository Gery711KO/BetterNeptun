package hu.kocsisgeri.betterneptun.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalLookaheadAnimationVisualDebugApi
import androidx.compose.animation.LookaheadAnimationVisualDebugging
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import androidx.core.view.WindowCompat
import androidx.navigation3.ui.LocalNavAnimatedContentScope

internal val LocalSharedTransitionScope =
    compositionLocalWithComputedDefaultOf<SharedTransitionScope> {
        error("No shared transition scope provided")
    }

object BetterNeptunTheme {
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val dimens: BetterNeptunDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalDimens.current
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

@Composable
fun BetterNeptunTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).run {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalDimens provides BetterNeptunDimens()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * Retrieves the current [SharedTransitionScope].
 *
 * This property provides a convenient way to access the scope required for defining
 * shared element transitions within Composable functions.
 */
val sharedTransitionScope: SharedTransitionScope
    @Composable get() = LocalSharedTransitionScope.current

/**
 * Provides the current [SharedTransitionScope] to the composition tree.
 * This allows child composables to access the transition scope using the [sharedTransitionScope]
 * property without needing to pass it explicitly through the hierarchy.
 *
 * @param content The composable content that will have access to the provided scope.
 */
@Composable
fun SharedTransitionScope.ProvideSharedTransitionScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
        content = content
    )
}

class PreviewThemeProvider : PreviewWrapperProvider {

    @SuppressLint("DisallowLookaheadAnimationVisualDebug")
    @OptIn(ExperimentalLookaheadAnimationVisualDebugApi::class)
    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        LookaheadAnimationVisualDebugging {
            AnimatedContent(true) { visible ->
                if (visible) BetterNeptunTheme(
                    darkTheme = isSystemInDarkTheme(),
                    content = {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = BetterNeptunTheme.colorScheme.background,
                            content = {
                                CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                                    SharedTransitionLayout {
                                        ProvideSharedTransitionScope(content)
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}
