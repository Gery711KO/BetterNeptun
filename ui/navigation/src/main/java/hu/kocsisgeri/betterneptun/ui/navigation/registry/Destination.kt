package hu.kocsisgeri.betterneptun.ui.navigation.registry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import kotlin.reflect.KClass

interface Destination<T: NavKey> {
    val key: KClass<T>
    val content: @Composable (T) -> Unit
}
