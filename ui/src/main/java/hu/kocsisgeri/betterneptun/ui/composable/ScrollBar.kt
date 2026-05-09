package hu.kocsisgeri.betterneptun.ui.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.transformLatest
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ScrollBar(
    modifier: Modifier,
    state: LazyListState,
    thumbComposable: @Composable @UiComposable (() -> Unit) = {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
        )
    }
) {
    var offsetY by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }
    var showThumb by remember { mutableStateOf(true) }

    val animatedOffsetY by animateIntAsState(offsetY)
    val animatedAlpha by animateFloatAsState(
        targetValue = if (showThumb) 1f else 0f,
        animationSpec = tween(500)
    )

    LaunchedEffect(state) {
        snapshotFlow {
            val totalItems = state.layoutInfo.totalItemsCount
            if (totalItems > 1) {
                (state.firstVisibleItemIndex.toFloat() / (totalItems - 1)).coerceIn(0f, 1f)
            } else {
                0f
            }
        }.collect { target ->
            progress = target
        }
    }

    LaunchedEffect(state) {
        snapshotFlow {
            state.isScrollInProgress
        }.transformLatest {
            if (!it) delay(1.seconds)
            emit(it)
        }.collect {
            showThumb = it
        }
    }

    Layout(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        content = {
            Box(modifier = Modifier.graphicsLayer { alpha = animatedAlpha }) {
                thumbComposable()
            }
        }
    ) { measurables, constraints ->
        // Measure the thumb
        val placeable = measurables.first().measure(
            constraints.copy(minWidth = 0, minHeight = 0)
        )

        val trackHeight = constraints.maxHeight
        val thumbHeight = placeable.height

        offsetY = lerp(start = 0, stop = trackHeight - thumbHeight, fraction = progress)

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.placeRelative(
                x = constraints.maxWidth - placeable.width,
                y = animatedOffsetY
            )
        }
    }
}
