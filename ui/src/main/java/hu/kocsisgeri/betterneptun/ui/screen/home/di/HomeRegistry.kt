package hu.kocsisgeri.betterneptun.ui.screen.home.di

import androidx.compose.runtime.Composable
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.navigation.registry.NavigationRegistry
import hu.kocsisgeri.betterneptun.ui.screen.home.HomeScreen
import org.koin.core.annotation.Singleton
import kotlin.reflect.KClass

@Singleton
internal class HomeRegistry: NavigationRegistry<HomeDestination> {

    override val navKey: KClass<HomeDestination> = HomeDestination::class

    @Composable
    override fun Content(destination: HomeDestination) {
        HomeScreen()
    }
}