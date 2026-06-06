package hu.kocsisgeri.betterneptun.ui.screen.login

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.localization.localized
import hu.kocsisgeri.betterneptun.ui.R
import hu.kocsisgeri.betterneptun.ui.core.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.core.theme.PreviewThemeProvider
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.screen.login.model.LoginState
import hu.kocsisgeri.betterneptun.ui.screen.login.model.isLoading
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
        onLoginClick = { viewModel.login() },
        onNeptunCodeChange = { viewModel.neptunCodeInput(it) },
        onPasswordChange = { viewModel.passwordInput(it) },
        onKeepMeLoggedInChange = { viewModel.keepMeLoggedIn(it) }
    )
}
@Composable
private fun LoginContent(
    isButtonEnabled: Boolean,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onNeptunCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onKeepMeLoggedInChange: (Boolean) -> Unit
) {
    var neptunCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var stayLoggedIn by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BetterNeptunTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(
            WindowInsets(
                BetterNeptunTheme.dimens.screenPadding,
                BetterNeptunTheme.dimens.screenPadding,
                BetterNeptunTheme.dimens.screenPadding,
                BetterNeptunTheme.dimens.screenPadding
            )
        ),
        bottomBar = {
            if (!isLoading) Button(
                onClick = onLoginClick,
                enabled = isButtonEnabled,
                modifier = Modifier
                    .padding(BetterNeptunTheme.dimens.screenPadding)
                    .navigationBarsPadding()
                    .imePadding()
                    .fillMaxWidth()
                    .alpha(if (isButtonEnabled) 1f else 0.6f),
                shape = BetterNeptunTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BetterNeptunTheme.colorScheme.primary,
                    contentColor = BetterNeptunTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(vertical = BetterNeptunTheme.dimens.itemSpacing)
            ) {
                Text(
                    text = LocalizationKey.LOGIN_SUBMIT.localized(),
                    style = BetterNeptunTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        LookaheadScope {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.animateBounds(lookaheadScope = this@LookaheadScope),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.oe_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .animateBounds(lookaheadScope = this@LookaheadScope)
                            .then(
                                if (!isLoading) Modifier.size(BetterNeptunTheme.dimens.logoSizeSmall)
                                else Modifier.size(BetterNeptunTheme.dimens.splashSize)
                            )

                    )
                    if (isLoading) {
                        Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.medium))
                        LinearProgressIndicator(
                            modifier = Modifier.width(BetterNeptunTheme.dimens.logoSizeSmall),
                            color = BetterNeptunTheme.colorScheme.primary
                        )
                    }
                }


                Crossfade(isLoading) { loading ->
                    if (!loading) {
                        Column {
                            Spacer(Modifier.weight(1f))

                            OutlinedTextField(
                                value = neptunCode,
                                onValueChange = {
                                    neptunCode = it
                                    onNeptunCodeChange(it)
                                },
                                label = { Text(LocalizationKey.LOGIN_INPUT_NEPTUN_CODE.localized()) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = BetterNeptunTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = BetterNeptunTheme.colorScheme.outline,
                                    focusedLabelColor = BetterNeptunTheme.colorScheme.primary,
                                    unfocusedLabelColor = BetterNeptunTheme.colorScheme.onSurfaceVariant,
                                    cursorColor = BetterNeptunTheme.colorScheme.primary,
                                    focusedTextColor = BetterNeptunTheme.colorScheme.onSurface,
                                    unfocusedTextColor = BetterNeptunTheme.colorScheme.onSurface,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                )
                            )

                            Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.medium))

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    onPasswordChange(it)
                                },
                                label = { Text(LocalizationKey.LOGIN_INPUT_PASSWORD.localized()) },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { onLoginClick() }
                                ),
                                trailingIcon = {
                                    val image = if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff

                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = image,
                                            contentDescription = null,
                                            tint = BetterNeptunTheme.colorScheme.primary
                                        )
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = BetterNeptunTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = BetterNeptunTheme.colorScheme.outline,
                                    focusedLabelColor = BetterNeptunTheme.colorScheme.primary,
                                    unfocusedLabelColor = BetterNeptunTheme.colorScheme.onSurfaceVariant,
                                    cursorColor = BetterNeptunTheme.colorScheme.primary,
                                    focusedTextColor = BetterNeptunTheme.colorScheme.onSurface,
                                    unfocusedTextColor = BetterNeptunTheme.colorScheme.onSurface,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                )
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = BetterNeptunTheme.dimens.medium),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Checkbox(
                                    checked = stayLoggedIn,
                                    onCheckedChange = {
                                        stayLoggedIn = it
                                        onKeepMeLoggedInChange(it)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = BetterNeptunTheme.colorScheme.primary,
                                        uncheckedColor = BetterNeptunTheme.colorScheme.outline
                                    )
                                )
                                Text(
                                    text = LocalizationKey.LOGIN_CHECKBOX_STAY_LOGGEDIN.localized(),
                                    color = BetterNeptunTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(start = BetterNeptunTheme.dimens.paddingSmall)
                                )
                            }
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun LoginContentPreview() {
    LoginContent(
        isButtonEnabled = true,
        isLoading = false,
        onLoginClick = {},
        onNeptunCodeChange = {},
        onPasswordChange = {},
        onKeepMeLoggedInChange = {}
    )
}

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun LoginContentLoadingPreview() {
    LoginContent(
        isButtonEnabled = false,
        isLoading = true,
        onLoginClick = {},
        onNeptunCodeChange = {},
        onPasswordChange = {},
        onKeepMeLoggedInChange = {}
    )
}
