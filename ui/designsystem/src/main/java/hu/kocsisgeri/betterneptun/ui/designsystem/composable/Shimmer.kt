package hu.kocsisgeri.betterneptun.ui.designsystem.composable

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberShimmerProgress(
    isLoading: Boolean = true,
    duration: Duration = 2.seconds
): State<Float> {
    return if (isLoading) {
        val transition = rememberInfiniteTransition(label = "SharedShimmerTransition")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = duration.inWholeMilliseconds.toInt(),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "SharedShimmerAnimation"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }
}

fun Modifier.sharedShimmer(
    progress: Float,
    cornerRadius: CornerRadius = CornerRadius(50f),
    shimmerColors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.1f),
        Color.LightGray.copy(alpha = 0.3f),
    )
): Modifier = this.drawBehind {
    val componentWidth = size.width
    val componentHeight = size.height
    val maxDimension = max(componentWidth, componentHeight)

    val offsetRange = maxDimension * 2f
    val currentOffset = (progress * offsetRange) - maxDimension

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = currentOffset, y = currentOffset),
        end = Offset(x = currentOffset + maxDimension, y = currentOffset + maxDimension)
    )

    drawRoundRect(
        brush = brush,
        cornerRadius = cornerRadius
    )
}

@Composable
fun randomTextSize() = remember {
    List(Random.nextInt(20, 50)) { " " }.joinToString("")
}

@Composable
fun RandomWidthBox(
    height: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth(remember { Random.nextDouble(0.2, 0.9).toFloat() })
    )
}
