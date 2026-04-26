package hu.kocsisgeri.betterneptun.ui.screen.login

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.ui.screen.login.model.LoginState
import hu.kocsisgeri.betterneptun.ui.screen.login.model.isLoading
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val context = LocalContext.current

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    val isLoading = remember(loginState) { loginState.isLoading() }

    val isButtonEnabled = remember(loginState) {
        (loginState as? LoginState.Idle)?.isButtonEnabled == true
    }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                navigator.navigateToInclusive(HomeDestination)
            }

            is LoginState.Error -> {
                Toast.makeText(context, state.errorMessage, Toast.LENGTH_LONG).show()
                viewModel.setIdle()
            }

            else -> {}
        }
    }

    LoginContent(
        isButtonEnabled = isButtonEnabled,
        isLoading = isLoading,
        onLoginClick = { viewModel.login(false) },
        onNeptunCodeChange = { viewModel.neptunCodeInput(it) },
        onPasswordChange = { viewModel.passwordInput(it) },
        onKeepMeLoggedInChange = { viewModel.keepMeLoggedIn(it) }
    )
}
