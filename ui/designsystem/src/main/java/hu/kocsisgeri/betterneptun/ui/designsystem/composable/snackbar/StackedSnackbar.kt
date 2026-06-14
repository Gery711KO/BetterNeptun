package hu.kocsisgeri.betterneptun.ui.designsystem.composable.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun StackedSnackbar(
    snackbarData: List<StackedSnackbarData>,
    maxStack: Int,
    animation: StackedSnackbarAnimation,
    onSnackbarRemoved: () -> Unit,
    firstItemVisibility: Boolean,
    modifier: Modifier = Modifier,
) {
    val snackbarDataSize = snackbarData.size
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(vertical = BetterNeptunTheme.dimens.screenPadding)
    ) {
        snackbarData.forEachIndexed { index, data ->
            val scaleMultiplier = snackbarData.size - index
            val scale = 1f.minus((scaleMultiplier).times(SnackbarConstant.SCALE_DECREMENT))

            val padding = ((index
                .times(SnackbarConstant.PADDING_INCREMENT))
                .plus(SnackbarConstant.PADDING_INCREMENT)).dp

            val scaleAnimation by animateFloatAsState(
                scale,
                animationSpec = animation.scaleAnimationSpec,
            )
            val initialPos by animateFloatAsState(
                0f,
                animationSpec = animation.scaleAnimationSpec,
            )
            val paddingAnimation by animateDpAsState(
                padding,
                animationSpec = animation.paddingAnimationSpec,
            )

            var offsetX by remember { mutableFloatStateOf(-1f) }

            AnimatedVisibility(
                visible = if (snackbarDataSize.dec() == index) firstItemVisibility else true,
                enter =
                    slideIn(
                        initialOffset =
                            { IntOffset(0, -SnackbarConstant.Y_TARGET_ENTER) },
                        animationSpec = animation.enterAnimationSpec,
                    ),
                exit =
                    if (offsetX == -1f) {
                        slideOut(
                            targetOffset =
                                { IntOffset(0, -SnackbarConstant.Y_TARGET_EXIT) },
                            animationSpec = animation.exitAnimationSpec,
                        )
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { if (offsetX > 0) SnackbarConstant.X_TARGET_EXIT_RIGHT else SnackbarConstant.X_TARGET_EXIT_LEFT },
                            animationSpec =
                                tween(
                                    easing = LinearEasing,
                                ),
                        )
                    },
            ) {
                val draggableModifier =
                    if (snackbarData.lastIndex == index) {
                        Modifier
                            .offset { IntOffset(offsetX.roundToInt(), 0) }
                            .draggable(
                                orientation = Orientation.Horizontal,
                                state =
                                    rememberDraggableState { delta ->
                                        offsetX += delta
                                    },
                                onDragStopped = {
                                    if (offsetX >= SnackbarConstant.OFFSET_THRESHOLD_EXIT_RIGHT || offsetX <= SnackbarConstant.OFFSET_THRESHOLD_EXIT_LEFT) {
                                        onSnackbarRemoved.invoke()
                                    } else {
                                        offsetX = initialPos
                                    }
                                },
                            )
                    } else {
                        Modifier
                    }
                val snackbarScale =
                    if (snackbarDataSize - index > maxStack) {
                        0f
                    } else {
                        scaleAnimation
                    }
                when (data) {
                    is StackedSnackbarData.Custom ->
                        CustomStackedSnackbarItem(
                            data = data,
                            scaleAnimation = snackbarScale,
                            paddingAnimation = paddingAnimation,
                            modifier = draggableModifier,
                            onActionClicked = {
                                onSnackbarRemoved.invoke()
                            },
                        )

                    is StackedSnackbarData.Normal ->
                        NormalStackedSnackbarItem(
                            data = data,
                            scaleAnimation = snackbarScale,
                            paddingAnimation = paddingAnimation,
                            modifier = draggableModifier,
                            onActionClicked = {
                                onSnackbarRemoved.invoke()
                                data.action?.invoke()
                            },
                        )
                }
            }
        }
    }
}

@Composable
private fun CustomStackedSnackbarItem(
    data: StackedSnackbarData.Custom,
    scaleAnimation: Float,
    paddingAnimation: Dp,
    onActionClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardSnackbarContainer(
        scaleAnimation = scaleAnimation,
        paddingAnimation = paddingAnimation,
        modifier = modifier,
        content = {
            Box(
                modifier =
                    Modifier
                        .background(BetterNeptunTheme.colorScheme.secondaryContainer)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(BetterNeptunTheme.shapes.medium)
                        .padding(BetterNeptunTheme.dimens.screenPadding),
            ) {
                data.content.invoke(onActionClicked)
            }
        },
    )
}

@Composable
private fun NormalStackedSnackbarItem(
    data: StackedSnackbarData.Normal,
    scaleAnimation: Float,
    paddingAnimation: Dp,
    onActionClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardSnackbarContainer(
        scaleAnimation = scaleAnimation,
        paddingAnimation = paddingAnimation,
        modifier = modifier,
        content = {
            Row(
                modifier =
                    Modifier
                        .background(BetterNeptunTheme.colorScheme.secondaryContainer)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(BetterNeptunTheme.shapes.medium)
                        .padding(BetterNeptunTheme.dimens.screenPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BetterNeptunTheme.dimens.small),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .size(BetterNeptunTheme.dimens.iconExtraLarge)
                            .clip(CircleShape)
                            .background(data.type.color.copy(alpha = 0.1f))
                            .padding(BetterNeptunTheme.dimens.small),
                ) {
                    Image(
                        imageVector = data.type.icon,
                        null,
                    )
                }

                Column {
                    Text(
                        text = data.title,
                        overflow = TextOverflow.Ellipsis,
                        style = BetterNeptunTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    if (data.description.isNullOrEmpty().not()) {
                        Text(
                            text = data.description,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = BetterNeptunTheme.typography.labelMedium,
                        )
                    }
                    if (data.actionTitle.isNullOrEmpty().not()) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = BetterNeptunTheme.dimens.extraSmall),
                            contentAlignment = Alignment.BottomEnd,
                        ) {
                            Text(
                                data.actionTitle,
                                modifier = Modifier.clickable {
                                    onActionClicked.invoke()
                                },
                                style = BetterNeptunTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = data.type.color,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun CardSnackbarContainer(
    scaleAnimation: Float,
    paddingAnimation: Dp,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        shape = BetterNeptunTheme.shapes.medium,
        modifier =
            Modifier
                .padding(
                    start = BetterNeptunTheme.dimens.medium,
                    end = BetterNeptunTheme.dimens.medium
                )
                .wrapContentHeight()
                .graphicsLayer {
                    translationY = paddingAnimation.toPx()
                    scaleX = scaleAnimation
                    scaleY = scaleAnimation
                }
                .then(modifier),
    ) {
        content.invoke()
    }
}