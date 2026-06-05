package hu.kocsisgeri.betterneptun.ui.core.modifier

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import hu.kocsisgeri.betterneptun.ui.navigation.utils.sharedTransitionScope

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Modifier.sharedBoundsAnimation(key: Any): Modifier = composed {
    with(sharedTransitionScope) {
        sharedBounds(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(200))
        )
    }
}