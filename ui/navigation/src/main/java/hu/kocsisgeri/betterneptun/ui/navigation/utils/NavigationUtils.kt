package hu.kocsisgeri.betterneptun.ui.navigation.utils

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import hu.kocsisgeri.betterneptun.ui.navigation.LocalSharedTransitionScope
import kotlinx.serialization.Serializable

val sharedTransitionScope: SharedTransitionScope
    @Composable get() = LocalSharedTransitionScope.current

fun <T : @Serializable NavKey> Scene<NavKey>.checkType(destination: T): Boolean {
    val destinationKey = destination.toString()

    return destinationKey == key.toString()
}
