package hu.kocsisgeri.betterneptun.ui.navigation.registry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import org.koin.core.module.Module
import kotlin.reflect.KClass

interface NavigationRegistry<T: NavKey> {

    val navKey: KClass<T>

    @Composable
    fun Content(destination: T)
}
