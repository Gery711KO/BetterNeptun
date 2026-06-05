package hu.kocsisgeri.betterneptun.ui.di

import hu.kocsisgeri.betterneptun.ui.navigation.destination.*
import hu.kocsisgeri.betterneptun.ui.navigation.registry.navigationEntry
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
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Single

/**
 * Dynamic destination provider approach.
 *
 * Creating a [navigationEntry] implementation will register the entry as a navigation destination.
 */
@Module
@Configuration
class FeatureModule {
    @Single @Qualifier(HomeDestination::class)
    fun home() = navigationEntry<HomeDestination> { HomeScreen() }

    @Single @Qualifier(LoadingDestination::class)
    fun loading() = navigationEntry<LoadingDestination> { LoadingScreen() }

    @Single @Qualifier(LoginDestination::class)
    fun login() = navigationEntry<LoginDestination> { LoginScreen() }

    @Single @Qualifier(MessagesDestination::class)
    fun messages() = navigationEntry<MessagesDestination> { MessagesScreen() }

    @Single @Qualifier(MessageDetailDestination::class)
    fun messageDetail() = navigationEntry<MessageDetailDestination> { MessageDetailScreen(it.messageId) }

    @Single @Qualifier(SemestersDestination::class)
    fun semesters() = navigationEntry<SemestersDestination> { SemestersScreen() }

    @Single @Qualifier(SettingsDestination::class)
    fun settings() = navigationEntry<SettingsDestination> { SettingsScreen() }

    @Single @Qualifier(SubjectsDestination::class)
    fun subjects() = navigationEntry<SubjectsDestination> { SubjectsScreen() }

    @Single @Qualifier(TimetableDestination::class)
    fun timetable() = navigationEntry<TimetableDestination> { TimetableScreen(it.selected) }
}
