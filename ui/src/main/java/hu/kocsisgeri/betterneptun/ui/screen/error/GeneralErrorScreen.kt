package hu.kocsisgeri.betterneptun.ui.screen.error

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorAction
import hu.kocsisgeri.betterneptun.domain.error.model.ErrorContent
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.designsystem.composable.LoadingLottie
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme.dimens
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme.typography
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import org.koin.androidx.compose.koinViewModel

@Composable
fun GeneralErrorScreen(
    viewModel: GeneralErrorViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val content by viewModel.latestErrorContent.collectAsStateWithLifecycle()

    BackHandler {
        // Keep user on error screen.
    }

    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        targetState = content,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        }
    ) { content ->
        ErrorScreenContent(
            content = content,
            isLoading = isLoading,
            onLaunchAction = viewModel::launchAction
        )
    }
}

@Composable
private fun ErrorScreenContent(
    content: ErrorContent.FullScreen?,
    isLoading: Boolean,
    onLaunchAction: (ErrorAction) -> Unit,
) {
    content?.let { content ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(dimens.screenPadding)
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        onClick = { onLaunchAction(content.primaryAction) }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
                            modifier = Modifier.animateContentSize()
                        ) {
                            Text(
                                text = content.primaryAction.label,
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            AnimatedVisibility(visible = isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(dimens.medium),
                                    color = ButtonDefaults.buttonColors().disabledContentColor
                                )
                            }
                        }
                    }
                    content.secondaryAction?.let { cta ->
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            onClick = { onLaunchAction(cta) }
                        ) {
                            Text(
                                text = cta.label,
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        ) { padding ->
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(content.icon)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(dimens.screenPadding)
            ) {
                Spacer(Modifier.weight(1f))
                Crossfade(isLoading) { loading ->
                    if (loading) LoadingLottie()
                    else LottieAnimation(
                        composition = composition,
                        modifier = Modifier.size(dimens.logoSizeSmall)
                    )
                }
                Text(
                    text = content.title,
                    style = typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = content.description,
                    style = typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.weight(2f))
            }
        }
    }
}

@PreviewWrapper(PreviewThemeProvider::class)
@PreviewLightDark
@Preview(
    name = "Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
)
@Composable
private fun GeneralErrorScreenPreview() {
    ErrorScreenContent(
        isLoading = false,
        content = ErrorContent.FullScreen(
            icon = R.raw.error_lottie,
            title = "Hiba",
            description = "Valami hiba történt, prőbáld újra később.",
            primaryAction = ErrorAction.Normal(
                label = "Újra",
                action = ErrorAction.PredefinedAction.NavigateBackToHome
            )
        ),
        onLaunchAction = { }
    )
}