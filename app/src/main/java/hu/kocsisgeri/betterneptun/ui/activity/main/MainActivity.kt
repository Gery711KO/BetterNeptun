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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import hu.kocsisgeri.betterneptun.domain.model.ThemeMode
import hu.kocsisgeri.betterneptun.domain.service.LocalizationService
import hu.kocsisgeri.betterneptun.localization.ProvideLocalization
import hu.kocsisgeri.betterneptun.localization.rememberLocalizationProviderScope
import hu.kocsisgeri.betterneptun.ui.core.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.themeMode.collect {
                    AppCompatDelegate.setDefaultNightMode(it.toAppCompatMode())
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            NonContentExtras()
            MainContent()
        }
    }

    private fun ThemeMode.toAppCompatMode() = when (this) {
        ThemeMode.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
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
}
