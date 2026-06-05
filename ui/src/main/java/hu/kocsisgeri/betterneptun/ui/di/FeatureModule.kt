package hu.kocsisgeri.betterneptun.ui.di

import androidx.compose.runtime.Composable
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoadingDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessageDetailDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessagesDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SemestersDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SettingsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SubjectsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.TimetableDestination
import hu.kocsisgeri.betterneptun.ui.navigation.registry.Destination
import hu.kocsisgeri.betterneptun.ui.screen.home.HomeScreen
import hu.kocsisgeri.betterneptun.ui.screen.loading.LoadingScreen
import hu.kocsisgeri.betterneptun.ui.screen.login.LoginScreen
import hu.kocsisgeri.betterneptun.ui.screen.messages.MessagesScreen
import hu.kocsisgeri.betterneptun.ui.screen.messages.detail.MessageDetailScreen
import hu.kocsisgeri.betterneptun.ui.screen.semesters.SemestersScreen
import hu.kocsisgeri.betterneptun.ui.screen.settings.SettingsScreen
import hu.kocsisgeri.betterneptun.ui.screen.subjects.SubjectsScreen
import hu.kocsisgeri.betterneptun.ui.screen.timetable.TimetableScreen
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.reflect.KClass

/**
 * Dynamic destination provider approach.
 *
 * Anything created as a [Destination] implementation will become an available screen.
 */
@Module
@Configuration
class FeatureModule

@Single
class HomeDestinationProvider : Destination<HomeDestination> {
    override val key: KClass<HomeDestination> = HomeDestination::class
    override val content: @Composable ((HomeDestination) -> Unit) = {
        HomeScreen()
    }
}

@Single
class LoadingDestinationProvider : Destination<LoadingDestination> {
    override val key: KClass<LoadingDestination> = LoadingDestination::class
    override val content: @Composable ((LoadingDestination) -> Unit) = {
        LoadingScreen()
    }
}

@Single
class LoginDestinationProvider : Destination<LoginDestination> {
    override val key: KClass<LoginDestination> = LoginDestination::class
    override val content: @Composable ((LoginDestination) -> Unit) = {
        LoginScreen()
    }
}

@Single
class MessagesDestinationProvider : Destination<MessagesDestination> {
    override val key: KClass<MessagesDestination> = MessagesDestination::class
    override val content: @Composable ((MessagesDestination) -> Unit) = {
        MessagesScreen()
    }
}

@Single
class MessageDetailDestinationProvider : Destination<MessageDetailDestination> {
    override val key: KClass<MessageDetailDestination> = MessageDetailDestination::class
    override val content: @Composable ((MessageDetailDestination) -> Unit) = {
        MessageDetailScreen(it.messageId)
    }
}

@Single
class SemestersDestinationProvider : Destination<SemestersDestination> {
    override val key: KClass<SemestersDestination> = SemestersDestination::class
    override val content: @Composable ((SemestersDestination) -> Unit) = {
        SemestersScreen()
    }
}

@Single
class SettingsDestinationProvider : Destination<SettingsDestination> {
    override val key: KClass<SettingsDestination> = SettingsDestination::class
    override val content: @Composable ((SettingsDestination) -> Unit) = {
        SettingsScreen()
    }
}

@Single
class SubjectsDestinationProvider : Destination<SubjectsDestination> {
    override val key: KClass<SubjectsDestination> = SubjectsDestination::class
    override val content: @Composable ((SubjectsDestination) -> Unit) = {
        SubjectsScreen()
    }
}

@Single
class TimetableDestinationProvider : Destination<TimetableDestination> {
    override val key: KClass<TimetableDestination> = TimetableDestination::class
    override val content: @Composable ((TimetableDestination) -> Unit) = {
        TimetableScreen(it.selected)
    }
}
