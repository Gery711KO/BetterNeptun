package hu.kocsisgeri.betterneptun.ui.designsystem.composable.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun rememberStackedSnackbarHostState(
    maxStack: Int = Int.MAX_VALUE,
    animation: StackedSnackbarAnimation = StackedSnackbarAnimation.Bounce,
) = run {
    val scope = rememberCoroutineScope()
    remember {
        StackedSnackbarHostState(animation = animation, maxStack = maxStack, coroutinesScope = scope)
    }
}
