package hu.kocsisgeri.betterneptun.ui.navigation.utils

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import hu.kocsisgeri.betterneptun.ui.navigation.LocalSharedTransitionScope
import hu.kocsisgeri.betterneptun.ui.navigation.transition.Transition
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

val sharedTransitionScope: SharedTransitionScope
    @Composable get() = LocalSharedTransitionScope.current

@Composable
fun SharedTransitionScope.ProvideSharedTransitionScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
        content = content
    )
}

fun <T : @Serializable NavKey> Scene<NavKey>.checkType(destination: KClass<T>): Boolean {
    val destinationKey = destination.java
    return destinationKey == key
}

fun <T : @Serializable Transition> Scene<NavKey>.isSubclassOf(transition: KClass<T>): Boolean {
    return transition.java.isAssignableFrom(key as Class<*>)
}
