package hu.kocsisgeri.betterneptun.ui.activity.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.domain.token.LogoutHandler
import hu.kocsisgeri.betterneptun.notification.NotificationScheduler
import hu.kocsisgeri.betterneptun.ui.core.Navigator
import hu.kocsisgeri.betterneptun.ui.core.checkType
import hu.kocsisgeri.betterneptun.ui.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.core.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.compose.navigation3.entryProvider
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.scope.Scope
import timber.log.Timber

@OptIn(KoinExperimentalAPI::class)
class MainActivity : AppCompatActivity(), AndroidScopeComponent {

    override val scope: Scope by activityRetainedScope()

    private val neptunRepository: NeptunRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val logoutHandler: LogoutHandler by inject()

    private val navigator: Navigator by inject()
    private val entryProvider by entryProvider<NavKey>()

    private val notificationScheduler: NotificationScheduler by inject()
    private val permissionHandler: PermissionHandler by inject()

    private val permissions by lazy {
        permissionHandler.getPermissions(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NonContentExtras()
            MainContent()
        }

        handleLogout()
        handleNotificationScheduling()
        handleThemeChange()
    }

    @Composable
    private fun NonContentExtras() {
        LaunchedEffect(navigator.currentScreen) {
            Timber.tag("Navigation").d("BackStack: ${navigator.backStack.toList()}")
        }
    }

    @Composable
    private fun MainContent() {
        BetterNeptunTheme {
            Navigator.DefaultNavDisplay(
                navigator = navigator,
                entryProvider = entryProvider,
                transitionSpec = {
                    val isFromLogin = initialState.checkType(LoginDestination)
                    val isToHome = targetState.checkType(HomeDestination)
                    val isToLogin = targetState.checkType(LoginDestination)

                    if ((isFromLogin && isToHome) || isToLogin) {
                        fadeIn() togetherWith fadeOut()
                    } else {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    }
                },
                popTransitionSpec = {
                    val isToLogin = targetState.checkType(LoginDestination)
                    if (isToLogin) {
                        fadeIn() togetherWith fadeOut()
                    } else {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    }
                },
                predictivePopTransitionSpec = {
                    val isToLogin = targetState.checkType(LoginDestination)
                    if (isToLogin) {
                        fadeIn() togetherWith fadeOut()
                    } else {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }

    private fun handleNotificationScheduling() {
        combine(
            neptunRepository.events,
            settingsRepository.notificationDelay,
            permissions,
        ) { events, delay, permissions ->
            if (
                permissions.isNotEmpty() &&
                permissions.count {
                    it.permissionState == PermissionData.State.Granted
                } == permissions.size
            ) {
                if (delay != -1) {
                    events.forEach { event ->
                        notificationScheduler.scheduleNotification(event, delay)
                    }
                } else {
                    events.forEach { event ->
                        notificationScheduler.cancelNotification(event.id)
                    }
                }
            } else {
                Timber.tag("Alarm").d("Needs permissions")
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleLogout() {
        logoutHandler.shouldForceLogout.onEach {
            navigator.navigateToInclusive(LoginDestination)
        }.launchIn(lifecycleScope)
    }

    private fun handleThemeChange() {
        settingsRepository.storedTheme.onEach {
            AppCompatDelegate.setDefaultNightMode(it.toAppCompatMode())
        }.launchIn(lifecycleScope)
    }

    private fun ThemeMode.toAppCompatMode() = when(this) {
        ThemeMode.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    }
}
