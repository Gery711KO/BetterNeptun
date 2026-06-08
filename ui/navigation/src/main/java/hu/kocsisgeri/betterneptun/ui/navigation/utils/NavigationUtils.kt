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

/**
 * Retrieves the current [SharedTransitionScope].
 *
 * This property provides a convenient way to access the scope required for defining
 * shared element transitions within Composable functions.
 */
val sharedTransitionScope: SharedTransitionScope
    @Composable get() = LocalSharedTransitionScope.current

/**
 * Provides the current [SharedTransitionScope] to the composition tree.
 * This allows child composables to access the transition scope using the [sharedTransitionScope]
 * property without needing to pass it explicitly through the hierarchy.
 *
 * @param content The composable content that will have access to the provided scope.
 */
@Composable
fun SharedTransitionScope.ProvideSharedTransitionScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
        content = content
    )
}

/**
 * Checks if the [Scene]'s key matches the specified [destination] class type.
 *
 * @param T The type of the navigation key.
 * @param destination The [KClass] of the navigation key to check against.
 * @return `true` if the scene's key class matches the provided destination class, `false` otherwise.
 */
fun <T : @Serializable NavKey> Scene<NavKey>.checkType(destination: KClass<T>): Boolean {
    val destinationKey = destination.java
    return destinationKey == key
}

/**
 * Checks if the [NavKey] of this [Scene] is a subclass of or the same class as the specified
 * [transition] class.
 *
 * @param transition The [KClass] to check against the scene's key.
 * @return `true` if the scene's key can be assigned to the given [transition] class,
 * `false` otherwise.
 */
fun <T : @Serializable Transition> Scene<NavKey>.isSubclassOf(transition: KClass<T>): Boolean {
    return transition.java.isAssignableFrom(key as Class<*>)
}
