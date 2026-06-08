package hu.kocsisgeri.betterneptun.ui.navigation.transition

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

/**
 * Defines a collection of pre-configured navigation transitions for the application's UI.
 *
 * This interface provides a set of static helper methods within its companion object
 * to create [ContentTransform] animations used by the navigation system to animate transitions
 * between different [Scene]s.
 */
interface Transition {

    companion object {

        fun sharedTransition(scope: AnimatedContentTransitionScope<Scene<NavKey>>) =
            with(scope) {
                fadeIn() togetherWith fadeOut()
            }

        fun slideTransition(scope: AnimatedContentTransitionScope<Scene<NavKey>>)  =
            with(scope) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            }

        fun popSlideTransition(scope: AnimatedContentTransitionScope<Scene<NavKey>>)  =
            with(scope) {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }

        fun splashTransition(scope: AnimatedContentTransitionScope<Scene<NavKey>>) =
            with(scope) {
                fadeIn(animationSpec = tween(500)) + slideIntoContainer(
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

        fun popSplashTransition(scope: AnimatedContentTransitionScope<Scene<NavKey>>) =
            with(scope) {
                fadeIn(animationSpec = tween(500)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(500, 300)
                ) + scaleIn(
                    initialScale = 0.6f,
                    animationSpec = tween(500, 300)
                ) togetherWith slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(500, 300),
                ) + scaleOut(
                    targetScale = 0.6f,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500, 500))
            }
    }
}
