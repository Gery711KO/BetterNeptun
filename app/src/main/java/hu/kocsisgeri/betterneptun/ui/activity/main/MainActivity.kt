package hu.kocsisgeri.betterneptun.ui.activity.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.domain.repository.login.LoginRepository
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.notification.NotificationScheduler
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.LoginDestination
import hu.kocsisgeri.betterneptun.ui.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.permission.model.PermissionData
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
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

    private val loginRepository: LoginRepository by inject()
    private val neptunRepository: NeptunRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

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
            LaunchedEffect(navigator.currentScreen) {
                Timber.tag("Navigation").d("BackStack: ${navigator.backStack.toList()}")
            }

            BetterNeptunTheme {
                Navigator.createNavDisplay(
                    navigator = navigator,
                    entryProvider = entryProvider,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        }

        loginRepository.forceLogOut.onEach {
            navigator.navigateToInclusive(LoginDestination)
        }.launchIn(lifecycleScope)

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
}
