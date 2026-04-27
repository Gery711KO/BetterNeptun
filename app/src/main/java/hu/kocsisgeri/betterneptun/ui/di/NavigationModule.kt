package hu.kocsisgeri.betterneptun.ui.di

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.compose.AndroidFragment
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessageDetailDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessagesDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SemestersDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SettingsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SubjectsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.TimetableDestination
import hu.kocsisgeri.betterneptun.ui.screen.home.HomeScreen
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
        Navigator.createNavigator(LoginDestination)
    }

    activityRetainedScope {
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

@Composable
inline fun <reified T: Fragment> ComposeFragment(
    noinline onUpdate: (T) -> Unit = {},
) {
    AndroidFragment<T>(
        modifier = Modifier.fillMaxSize(),
        arguments = Bundle.EMPTY,
        onUpdate = onUpdate
    )
}