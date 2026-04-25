package hu.kocsisgeri.betterneptun.ui.di

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.compose.AndroidFragment
import hu.kocsisgeri.betterneptun.ui.home.HomeFragment
import hu.kocsisgeri.betterneptun.ui.login.LoginScreen
import hu.kocsisgeri.betterneptun.ui.messages.MessagesFragment
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessagesDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SemestersDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SettingsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.SubjectsDestination
import hu.kocsisgeri.betterneptun.ui.navigation.destination.TimetableDestination
import hu.kocsisgeri.betterneptun.ui.semesters.SemestersFragment
import hu.kocsisgeri.betterneptun.ui.settings.SettingsFragment
import hu.kocsisgeri.betterneptun.ui.subjects.SubjectsFragment
import hu.kocsisgeri.betterneptun.ui.timetable.TimetableFragment
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
            ComposeFragment<HomeFragment>()
        }

        navigation<SettingsDestination> {
            ComposeFragment<SettingsFragment>()
        }

        navigation<MessagesDestination> {
            ComposeFragment<MessagesFragment>()
        }

        navigation<SemestersDestination> {
            ComposeFragment<SemestersFragment>()
        }

        navigation<SubjectsDestination> {
            ComposeFragment<SubjectsFragment>()
        }

        navigation<TimetableDestination> {
            ComposeFragment<TimetableFragment>()
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