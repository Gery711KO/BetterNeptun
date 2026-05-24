package hu.kocsisgeri.betterneptun.ui.activity.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.repository.neptun.NeptunRepository
import hu.kocsisgeri.betterneptun.domain.repository.settings.SettingsRepository
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.localization.ProvideLocalization
import hu.kocsisgeri.betterneptun.localization.rememberLocalizationProviderScope
import hu.kocsisgeri.betterneptun.notification.NotificationScheduler
import hu.kocsisgeri.betterneptun.ui.core.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val navigator: Navigator by inject()

    private val localizationService: LocalizationService by inject()
    private val notificationScheduler: NotificationScheduler by inject()

    private val neptunRepository: NeptunRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val permissionHandler: PermissionHandler by inject()
    private val permissions by lazy {
        permissionHandler.getPermissions(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }

        startUserRelatedActions()

        enableEdgeToEdge()
        setContent {
            NonContentExtras()
            MainContent()
        }
    }

    @Composable
    private fun NonContentExtras() {
        LaunchedEffect(navigator.currentScreen) {
            Timber.tag("Navigation").d("BackStack: ${navigator.backStack.toList()}")
        }
    }

    @Composable
    private fun MainContent() {
        val localizationProviderScope = rememberLocalizationProviderScope(localizationService)

        BetterNeptunTheme {
            localizationProviderScope.ProvideLocalization {
                navigator.Content()
            }
        }
    }

    private fun startUserRelatedActions() {
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
                        notificationScheduler.scheduleNotification(
                            context = this,
                            item = event,
                            delayMinutes = delay
                        )
                    }
                } else {
                    events.forEach { event ->
                        notificationScheduler.cancelNotification(
                            context = this,
                            itemId = event.id
                        )
                    }
                }
            } else {
                Timber.tag("Alarm").d("Needs permissions")
            }
        }.launchIn(lifecycleScope)

        settingsRepository.storedTheme.onEach {
            AppCompatDelegate.setDefaultNightMode(it.toAppCompatMode())
        }.launchIn(lifecycleScope)
    }

    private fun ThemeMode.toAppCompatMode() = when (this) {
        ThemeMode.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    }
}
