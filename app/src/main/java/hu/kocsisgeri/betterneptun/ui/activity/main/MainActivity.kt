package hu.kocsisgeri.betterneptun.ui.activity.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.localization.ProvideLocalization
import hu.kocsisgeri.betterneptun.localization.rememberLocalizationProviderScope
import hu.kocsisgeri.betterneptun.ui.core.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by inject()
    private val navigator: Navigator by inject()
    private val permissionHandler: PermissionHandler by inject()

    private val localizationService: LocalizationService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setOnExitAnimationListener { it.remove() }
        super.onCreate(savedInstanceState)

        permissionHandler.getPermissions(this).launchIn(lifecycleScope)

        viewModel.themeMode.onEach {
            AppCompatDelegate.setDefaultNightMode(it.toAppCompatMode())
        }.launchIn(lifecycleScope)

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

        BetterNeptunTheme(darkTheme = isSystemInDarkTheme()) {
            localizationProviderScope.ProvideLocalization {
                navigator.Content()
            }
        }
    }

    private fun ThemeMode.toAppCompatMode() = when (this) {
        ThemeMode.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    }
}
