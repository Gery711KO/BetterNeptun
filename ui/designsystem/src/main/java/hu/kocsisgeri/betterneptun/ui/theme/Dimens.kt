package hu.kocsisgeri.betterneptun.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class BetterNeptunDimens(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val giant: Dp = 40.dp,

    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 16.dp,
    val paddingLarge: Dp = 20.dp,
    val paddingExtraLarge: Dp = 24.dp,
    
    val screenPadding: Dp = 16.dp,
    val itemSpacing: Dp = 12.dp,
    val groupSpacing: Dp = 16.dp,
    
    val iconExtraSmall: Dp = 12.dp,
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 28.dp,
    val iconExtraLarge: Dp = 32.dp,
    val iconHuge: Dp = 48.dp,
    val iconGiant: Dp = 64.dp,

    val badgeSize: Dp = 8.dp,
    val dividerThickness: Dp = 0.5.dp,

    val splashSize: Dp = 192.dp,
    val logoSizeSmall: Dp = 120.dp,
    val retryButtonWidth: Dp = 100.dp
)

val LocalDimens = staticCompositionLocalOf { BetterNeptunDimens() }
