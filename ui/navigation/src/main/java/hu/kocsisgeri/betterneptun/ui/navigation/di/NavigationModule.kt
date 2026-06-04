package hu.kocsisgeri.betterneptun.ui.navigation.di

import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoadingDestination
import hu.kocsisgeri.betterneptun.ui.navigation.registry.NavigationRegistry
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.annotation.Singleton

@Module
@Configuration
class NavigationModule {

    @Singleton
    fun provideNavigationDestinations(
        destination: List<NavigationRegistry<NavKey>>
    ): List<NavigationRegistry<NavKey>> = destination

    @Single
    fun provideNavigator(
        destinations: List<NavigationRegistry<NavKey>>
    ): Navigator = Navigator.createNavigator(
        startDestination = LoadingDestination,
        destinations = destinations
    )
}
