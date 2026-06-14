package hu.kocsisgeri.betterneptun.ui.screen.loading

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import kotlinx.coroutines.flow.filterNotNull
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LoadingScreen(
    viewModel: LoadingViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val initializerState by viewModel.initializationState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.nextDestination.filterNotNull().collect {
            navigator.navigateToInclusive(it)
        }
    }

    LoadingScreenContent(initializerState = initializerState)
}

@Composable
private fun LoadingScreenContent(initializerState: Initializer.State) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = initializerState,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { state ->
            when (state) {
                Initializer.State.Idle,
                Initializer.State.Initialized,
                is Initializer.State.Error -> SplashLogo()

                is Initializer.State.Initializing -> LoadingContent()
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column {
        SplashLogo()
        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.medium))
        LinearProgressIndicator(
            modifier = Modifier.width(BetterNeptunTheme.dimens.splashSize),
            color = BetterNeptunTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SplashLogo() {
    Image(
        painter = painterResource(id = R.drawable.oe_logo),
        contentDescription = null,
        modifier = Modifier.size(BetterNeptunTheme.dimens.splashSize)
    )
}

@Preview
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun IdlePreview() {
    LoadingScreenContent(
        initializerState = Initializer.State.Idle,
    )
}

@Preview
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun InitializingPreview() {
    LoadingScreenContent(
        initializerState = Initializer.State.Initializing,
    )
}


@Preview
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun InitializedPreview() {
    LoadingScreenContent(
        initializerState = Initializer.State.Initialized,
    )
}
