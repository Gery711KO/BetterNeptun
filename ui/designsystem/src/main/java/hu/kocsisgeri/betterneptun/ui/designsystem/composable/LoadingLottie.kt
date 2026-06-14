package hu.kocsisgeri.betterneptun.ui.designsystem.composable

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme

@Composable
fun LoadingLottie(size: Dp = BetterNeptunTheme.dimens.logoSizeSmall) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_lottie)
    )

    LottieAnimation(
        composition = composition,
        modifier = Modifier.size(size),
        speed = 1f,
        iterations = Int.MAX_VALUE
    )
}
