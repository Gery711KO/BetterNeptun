package hu.kocsisgeri.betterneptun.ui.designsystem.composable.snackbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset

@Stable
enum class StackedSnackbarAnimation(
    val paddingAnimationSpec: AnimationSpec<Dp>,
    val scaleAnimationSpec: AnimationSpec<Float>,
    val enterAnimationSpec: FiniteAnimationSpec<IntOffset>,
    val exitAnimationSpec: FiniteAnimationSpec<IntOffset>,
) {
    Bounce(
        paddingAnimationSpec =
            spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioHighBouncy,
            ),
        scaleAnimationSpec =
            spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioHighBouncy,
            ),
        enterAnimationSpec =
            spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioHighBouncy,
                visibilityThreshold = IntOffset.VisibilityThreshold,
            ),
        exitAnimationSpec =
            spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioHighBouncy,
                visibilityThreshold = IntOffset.VisibilityThreshold,
            ),
    ),
    Slide(
        paddingAnimationSpec =
            tween(
                SnackbarConstant.TWEEN_ANIMATION_DURATION,
                0,
                FastOutSlowInEasing,
            ),
        scaleAnimationSpec =
            tween(
                SnackbarConstant.TWEEN_ANIMATION_DURATION,
                0,
                FastOutSlowInEasing,
            ),
        enterAnimationSpec =
            tween(
                SnackbarConstant.TWEEN_ANIMATION_DURATION,
                0,
                FastOutSlowInEasing,
            ),
        exitAnimationSpec =
            tween(
                SnackbarConstant.TWEEN_ANIMATION_DURATION,
                0,
                FastOutSlowInEasing,
            ),
    ),
}

object SnackbarColor {
    val Link = Color(0xFF246EE5)
    val Info = Color(0xFF3150EC)
    val Warning = Color(0xFFFE9E01)
    val Error = Color(0xFFF54F4E)
    val Success = Color(0xFF24BF5F)
}

@Stable
enum class StackedSnackbarDuration {
    Short,
    Long,
    Indefinite,
}

internal object SnackbarConstant {
    const val SCALE_DECREMENT = 0.05f
    const val PADDING_INCREMENT = 16
    const val Y_TARGET_ENTER = 100
    const val Y_TARGET_EXIT = 750
    const val X_TARGET_EXIT_RIGHT = 1000
    const val X_TARGET_EXIT_LEFT = -1000
    const val OFFSET_THRESHOLD_EXIT_LEFT = -350
    const val OFFSET_THRESHOLD_EXIT_RIGHT = 350
    const val TWEEN_ANIMATION_DURATION = 100
}
