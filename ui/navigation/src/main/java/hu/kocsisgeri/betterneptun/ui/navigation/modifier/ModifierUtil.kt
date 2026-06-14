package hu.kocsisgeri.betterneptun.ui.navigation.modifier

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import hu.kocsisgeri.betterneptun.ui.theme.sharedTransitionScope

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Modifier.sharedBoundsAnimation(key: Any): Modifier = composed {
    with(sharedTransitionScope) {
        sharedBounds(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.FillBounds),
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(200))
        )
    }
}

fun Modifier.renderShared(): Modifier = composed {
    with(sharedTransitionScope) {
        renderInSharedTransitionScopeOverlay()
    }
}

@Composable
fun isLandscape() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
