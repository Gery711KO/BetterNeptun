package hu.kocsisgeri.betterneptun.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

interface SlideTransition {

    companion object {

        fun AnimatedContentTransitionScope<Scene<NavKey>>.transition() : ContentTransform {
            return slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
        }
    }
}