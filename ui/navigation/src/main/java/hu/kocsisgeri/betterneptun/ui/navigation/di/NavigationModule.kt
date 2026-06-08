package hu.kocsisgeri.betterneptun.ui.navigation.di

import androidx.compose.material3.Text
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoadingDestination
import hu.kocsisgeri.betterneptun.ui.navigation.registry.NavigationRegistry
import hu.kocsisgeri.betterneptun.ui.navigation.registry.navigationEntry
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Single

@Module
@Configuration
class NavigationModule {

    @Single
    fun provideNavigator(
        registry: NavigationRegistry
    ): Navigator = Navigator.createNavigator(
        startDestination = LoadingDestination,
        navigationEntries = registry.navigationEntries
    )
}

@Module
internal class SampleModule {

    @Serializable
    private class SampleDestination: NavKey

    @Single @Qualifier(SampleDestination::class)
    fun sampleScreen() = navigationEntry<SampleDestination> {
        Text("Preview content")
    }
}
