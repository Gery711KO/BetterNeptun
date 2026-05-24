package hu.kocsisgeri.betterneptun.ui.di

import hu.kocsisgeri.betterneptun.ui.core.Navigator
import hu.kocsisgeri.betterneptun.ui.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.destination.LoadingDestination
import hu.kocsisgeri.betterneptun.ui.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.destination.MessageDetailDestination
import hu.kocsisgeri.betterneptun.ui.destination.MessagesDestination
import hu.kocsisgeri.betterneptun.ui.destination.SemestersDestination
import hu.kocsisgeri.betterneptun.ui.destination.SettingsDestination
import hu.kocsisgeri.betterneptun.ui.destination.SubjectsDestination
import hu.kocsisgeri.betterneptun.ui.destination.TimetableDestination
import hu.kocsisgeri.betterneptun.ui.screen.home.HomeScreen
import hu.kocsisgeri.betterneptun.ui.screen.loading.LoadingScreen
import hu.kocsisgeri.betterneptun.ui.screen.login.LoginScreen
import hu.kocsisgeri.betterneptun.ui.screen.messages.MessagesScreen
import hu.kocsisgeri.betterneptun.ui.screen.messages.detail.MessageDetailScreen
import hu.kocsisgeri.betterneptun.ui.screen.semesters.SemestersScreen
import hu.kocsisgeri.betterneptun.ui.screen.settings.SettingsScreen
import hu.kocsisgeri.betterneptun.ui.screen.subjects.SubjectsScreen
import hu.kocsisgeri.betterneptun.ui.screen.timetable.TimetableScreen
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    single<Navigator> {
        Navigator.createNavigator(LoadingDestination)
    }

    activityRetainedScope {
        navigation<LoadingDestination> {
            LoadingScreen()
        }

        navigation<LoginDestination> {
            LoginScreen()
        }

        navigation<HomeDestination> {
            HomeScreen()
        }

        navigation<SettingsDestination> {
            SettingsScreen()
        }

        navigation<MessagesDestination> {
            MessagesScreen()
        }

        navigation<MessageDetailDestination> {
            MessageDetailScreen(it.messageId)
        }

        navigation<SemestersDestination> {
            SemestersScreen()
        }

        navigation<SubjectsDestination> {
            SubjectsScreen()
        }

        navigation<TimetableDestination> {
            TimetableScreen()
        }
    }
}
