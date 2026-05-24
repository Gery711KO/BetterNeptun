package hu.kocsisgeri.betterneptun.ui.screen.loading

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.domain.initializable.Initializer
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.core.theme.PreviewThemeProvider
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
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

    LoadingScreenContent(
        initializerState = initializerState,
        onInitialize = viewModel::initialize
    )
}

@Composable
private fun LoadingScreenContent(
    initializerState: Initializer.State,
    onInitialize: () -> Unit,
) {
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
                Initializer.State.Initialized -> SplashLogo()
                is Initializer.State.Initializing -> LoadingContent()
                is Initializer.State.Error -> ErrorContent(
                    message = state.errorMessage,
                    onRetry = onInitialize
                )
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
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = Icons.Rounded.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(BetterNeptunTheme.dimens.splashSize),
            colorFilter = ColorFilter.tint(BetterNeptunTheme.colorScheme.error)
        )
        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.medium))
        Text(
            text = message,
            color = BetterNeptunTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.medium))
        IconButton (
            modifier = Modifier.width(BetterNeptunTheme.dimens.retryButtonWidth),
            onClick = onRetry,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = BetterNeptunTheme.colorScheme.error,
                contentColor = BetterNeptunTheme.colorScheme.onError,
            ),
            content = {
                Icon(
                    imageVector = Icons.Rounded.Replay,
                    contentDescription = null
                )
            }
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
        onInitialize = {}
    )
}

@Preview
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun InitializingPreview() {
    LoadingScreenContent(
        initializerState = Initializer.State.Initializing,
        onInitialize = {}
    )
}


@Preview
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun InitializedPreview() {
    LoadingScreenContent(
        initializerState = Initializer.State.Initialized,
        onInitialize = {}
    )
}

@Preview
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
private fun ErrorPreview() {
    LoadingScreenContent(
        initializerState = Initializer.State.Error(
            errorMessage = "Something went wrong"
        ),
        onInitialize = {}
    )
}
