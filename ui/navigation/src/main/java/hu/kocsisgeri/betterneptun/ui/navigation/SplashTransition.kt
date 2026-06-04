package hu.kocsisgeri.betterneptun.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

interface SplashTransition {

    companion object {

        fun AnimatedContentTransitionScope<Scene<NavKey>>.transition() : ContentTransform {
            return fadeIn(animationSpec = tween(500)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(500, 300)
            ) + scaleIn(
                initialScale = 0.6f,
                animationSpec = tween(500, 300)
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(500, 300),
            ) + scaleOut(
                targetScale = 0.6f,
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500, 500))
        }
    }
}