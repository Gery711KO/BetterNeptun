package hu.kocsisgeri.betterneptun.ui.screen.loading.di

import androidx.compose.runtime.Composable
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoadingDestination
import hu.kocsisgeri.betterneptun.ui.navigation.registry.NavigationRegistry
import hu.kocsisgeri.betterneptun.ui.screen.loading.LoadingScreen
import org.koin.core.annotation.Singleton
import kotlin.reflect.KClass

@Singleton(createdAtStart = true)
internal class LoadingRegistry: NavigationRegistry<LoadingDestination> {

    override val navKey: KClass<LoadingDestination> = LoadingDestination::class

    @Composable
    override fun Content(destination: LoadingDestination) {
        LoadingScreen()
    }
}