package hu.kocsisgeri.betterneptun.ui.navigation.registry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Single
import kotlin.reflect.KClass

/**
 * Represents a single entry in the navigation registry, mapping a specific [NavKey]
 * type to its corresponding Composable UI implementation.
 *
 * @property key The [KClass] of the navigation key used to identify this destination.
 * @property content A composable function that renders the UI for the given [NavKey].
 */
interface NavigationEntry {
    val key: KClass<out NavKey>
    val content: @Composable (NavKey) -> Unit
}


/**
 * Creates a [NavigationEntry] for a specific [NavKey] type.
 *
 * Used in dependency injection [Module]s to register screens to the navigation graph.
 *
 * **IMPORTANT**: Always annotate with [Single] and [Qualifier] where the function is used with [T]
 * in the annotation value.
 *
 * @param T The specific type of [NavKey] that this entry handles.
 *
 * @see [hu.kocsisgeri.betterneptun.ui.navigation.di.SampleModule]
 */
inline fun <reified T : NavKey> navigationEntry(
    noinline content: @Composable (T) -> Unit
): NavigationEntry = object : NavigationEntry {
    override val key: KClass<out NavKey> = T::class
    override val content: @Composable ((NavKey) -> Unit) = {
        content(it as T)
    }
}
