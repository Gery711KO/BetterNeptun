package hu.kocsisgeri.betterneptun.ui.screen.login.di

import androidx.compose.runtime.Composable
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.navigation.registry.NavigationRegistry
import hu.kocsisgeri.betterneptun.ui.screen.login.LoginScreen
import org.koin.core.annotation.Singleton
import kotlin.reflect.KClass

@Singleton(createdAtStart = true)
internal class LoginRegistry: NavigationRegistry<LoginDestination> {

    override val navKey: KClass<LoginDestination> = LoginDestination::class

    @Composable
    override fun Content(destination: LoginDestination) {
        LoginScreen()
    }
}