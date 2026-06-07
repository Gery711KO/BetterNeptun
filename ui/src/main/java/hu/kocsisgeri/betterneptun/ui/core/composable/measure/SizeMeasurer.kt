package hu.kocsisgeri.betterneptun.ui.core.composable.measure

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun SizeMeasurer(
    content: @Composable SizeMeasurerScope.() -> Unit
) {
    val scope = rememberSaveable(saver = SizeMeasurerScopeImpl.SAVER) { SizeMeasurerScopeImpl() }
    val unmodifiedScope = remember { SizeMeasurerScopeImpl() }

    val movableContent = remember {
        movableContentWithReceiverOf<SizeMeasurerScope> { Box { content() } }
    }

    SubcomposeLayout { constraints ->
        val measured = subcompose(SizeMeasurerScopeImpl.Slot.MEASURE) {
            movableContent(unmodifiedScope)
        }.first().measure(constraints)

        val placed = subcompose(SizeMeasurerScopeImpl.Slot.PLACE) {
            movableContent(scope)
        }.first().measure(constraints)

        scope.size = DpSize(
            width = measured.width.toDp(),
            height = measured.height.toDp()
        )

        layout(
            height = measured.height,
            width = measured.width
        ) {
            placed.place(x = 0, y = 0)
        }
    }
}

@Composable
fun SizeMeasurer(
    measured: @Composable () -> Unit,
    content: @Composable SizeMeasurerScope.() -> Unit
) {
    val scope = rememberSaveable(saver = SizeMeasurerScopeImpl.SAVER) { SizeMeasurerScopeImpl() }

    SubcomposeLayout { constraints ->
        val measured = subcompose(SizeMeasurerScopeImpl.Slot.MEASURE) {
            measured()
        }.firstOrNull()?.measure(constraints)

        val placed = subcompose(SizeMeasurerScopeImpl.Slot.PLACE) {
            content(scope)
        }.firstOrNull()?.measure(constraints)

        scope.size = DpSize(
            width = measured?.width?.toDp() ?: 0.dp,
            height = measured?.height?.toDp() ?: 0.dp
        )

        layout(
            height = measured?.height?: constraints.minHeight,
            width = measured?.width?: constraints.minWidth
        ) {
            placed?.place(x = 0, y = 0)
        }
    }
}

interface SizeMeasurerScope {

    val size: DpSize
    val isSizeMeasured: Boolean

    fun Modifier.fillHeight(): Modifier
    fun Modifier.fillWidth(): Modifier
    fun Modifier.fillAvailableSpace(): Modifier

    fun Modifier.maxHeight(height: Dp): Modifier
    fun Modifier.maxWidth(width: Dp): Modifier
    fun Modifier.maxSize(width: Dp, height: Dp): Modifier
}

@Stable
private class SizeMeasurerScopeImpl : SizeMeasurerScope {

    override var size: DpSize by mutableStateOf(DpSize.Zero)
    override val isSizeMeasured: Boolean by derivedStateOf { size != DpSize.Zero }

    override fun Modifier.maxHeight(height: Dp) =
        ifMeasured { heightIn(max = height) }

    override fun Modifier.maxWidth(width: Dp) =
        ifMeasured { widthIn(max = width) }

    override fun Modifier.maxSize(width: Dp, height: Dp) =
        ifMeasured { sizeIn(maxHeight = height, maxWidth = width) }

    override fun Modifier.fillHeight(): Modifier =
        ifMeasured { heightIn(max = size.height).fillMaxHeight() }

    override fun Modifier.fillWidth(): Modifier =
        ifMeasured { widthIn(max = size.width).fillMaxWidth() }

    override fun Modifier.fillAvailableSpace(): Modifier =
        ifMeasured { sizeIn(maxWidth = size.width, maxHeight = size.height).fillMaxSize() }

    private fun Modifier.ifMeasured(
        modifier: Modifier.() -> Modifier
    ): Modifier = this.then(
        if (isSizeMeasured) Modifier.modifier()
        else Modifier
    )

    companion object {
        val SAVER = Saver<SizeMeasurerScopeImpl, Pair<Float, Float>>(
            save = { scope ->
                Pair(scope.size.width.value, scope.size.height.value)
            },
            restore = { (width, height) ->
                SizeMeasurerScopeImpl().apply {
                    size = DpSize(width.dp, height.dp)
                }
            }
        )
    }

    enum class Slot {
        MEASURE,
        PLACE
    }
}
