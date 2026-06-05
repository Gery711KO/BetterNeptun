package hu.kocsisgeri.betterneptun.ui.navigation.registry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import kotlin.reflect.KClass

interface NavigationEntry {
    val key: KClass<out NavKey>
    val content: @Composable (NavKey) -> Unit
}

inline fun <reified T : NavKey> navigationEntry(
    noinline content: @Composable (T) -> Unit
): NavigationEntry = object : NavigationEntry {
    override val key: KClass<out NavKey> = T::class
    override val content: @Composable ((NavKey) -> Unit) = {
        content(it as T)
    }
}
