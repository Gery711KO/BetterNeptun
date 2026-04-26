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
import androidx.navigation3.runtime.NavKey
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
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
    val navigator: Navigator by inject()
    val entryProvider by entryProvider<NavKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaunchedEffect(navigator.currentScreen) {
                Timber.tag("Navigation").d("Current: ${navigator.currentScreen}")
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
    }
}