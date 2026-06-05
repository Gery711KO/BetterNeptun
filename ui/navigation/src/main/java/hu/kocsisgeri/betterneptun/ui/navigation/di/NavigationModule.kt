package hu.kocsisgeri.betterneptun.ui.navigation.di

import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoadingDestination
import hu.kocsisgeri.betterneptun.ui.navigation.registry.NavigationRegistry
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration
class NavigationModule {

    @Single
    fun provideNavigator(
        registry: NavigationRegistry
    ): Navigator = Navigator.createNavigator(
        startDestination = LoadingDestination,
        destinations = registry.destinations
    )
}
