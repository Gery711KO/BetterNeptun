package hu.kocsisgeri.betterneptun.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

interface SharedBoundsTransition {

    companion object {

        fun AnimatedContentTransitionScope<Scene<NavKey>>.transition() : ContentTransform {
            return scaleIn(
                initialScale = 0.3f
            ) togetherWith scaleOut(
                targetScale = 0.3f
            )
        }
    }
}