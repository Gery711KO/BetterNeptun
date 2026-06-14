package hu.kocsisgeri.betterneptun.ui.screen.login

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.kocsisgeri.betterneptun.localization.LocalizationKey
import hu.kocsisgeri.betterneptun.localization.localized
import hu.kocsisgeri.betterneptun.ui.designsystem.R
import hu.kocsisgeri.betterneptun.ui.navigation.Navigator
import hu.kocsisgeri.betterneptun.ui.navigation.destination.HomeDestination
import hu.kocsisgeri.betterneptun.ui.screen.login.model.LoginState
import hu.kocsisgeri.betterneptun.ui.screen.login.model.isLoading
import hu.kocsisgeri.betterneptun.ui.theme.BetterNeptunTheme
import hu.kocsisgeri.betterneptun.ui.theme.PreviewThemeProvider
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
        isLandscape = isLandscape,
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
    isLandscape: Boolean,
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
            if (!isLoading && !isLandscape) {
                LoginSubmitButton(
                    onClick = onLoginClick,
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .padding(BetterNeptunTheme.dimens.screenPadding)
                        .navigationBarsPadding()
                        .imePadding()
                )
            }
        }
    ) { padding ->
        LookaheadScope {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    LoginLogo(
                        isLoading = isLoading,
                        lookaheadScope = this@LookaheadScope,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isLoading) {
                        Column(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = BetterNeptunTheme.dimens.medium),
                            verticalArrangement = Arrangement.Center
                        ) {
                            LoginFormFields(
                                neptunCode = neptunCode,
                                onNeptunCodeChange = {
                                    neptunCode = it
                                    onNeptunCodeChange(it)
                                },
                                password = password,
                                onPasswordChange = {
                                    password = it
                                    onPasswordChange(it)
                                },
                                passwordVisible = passwordVisible,
                                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                                stayLoggedIn = stayLoggedIn,
                                onStayLoggedInChange = {
                                    stayLoggedIn = it
                                    onKeepMeLoggedInChange(it)
                                },
                                onLoginClick = onLoginClick
                            )

                            LoginSubmitButton(
                                onClick = onLoginClick,
                                enabled = isButtonEnabled
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoginLogo(
                        isLoading = isLoading,
                        lookaheadScope = this@LookaheadScope
                    )

                    Crossfade(isLoading) { loading ->
                        if (!loading) {
                            Column {
                                Spacer(Modifier.weight(1f))
                                LoginFormFields(
                                    neptunCode = neptunCode,
                                    onNeptunCodeChange = {
                                        neptunCode = it
                                        onNeptunCodeChange(it)
                                    },
                                    password = password,
                                    onPasswordChange = {
                                        password = it
                                        onPasswordChange(it)
                                    },
                                    passwordVisible = passwordVisible,
                                    onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                                    stayLoggedIn = stayLoggedIn,
                                    onStayLoggedInChange = {
                                        stayLoggedIn = it
                                        onKeepMeLoggedInChange(it)
                                    },
                                    onLoginClick = onLoginClick
                                )
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginLogo(
    isLoading: Boolean,
    lookaheadScope: LookaheadScope,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.animateBounds(lookaheadScope = lookaheadScope),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.oe_logo),
            contentDescription = null,
            modifier = Modifier
                .animateBounds(lookaheadScope = lookaheadScope)
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
}

@Composable
private fun LoginFormFields(
    neptunCode: String,
    onNeptunCodeChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    stayLoggedIn: Boolean,
    onStayLoggedInChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit
) {
    BetterNeptunTextField(
        value = neptunCode,
        onValueChange = onNeptunCodeChange,
        label = LocalizationKey.LOGIN_INPUT_NEPTUN_CODE.localized(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )

    Spacer(modifier = Modifier.height(BetterNeptunTheme.dimens.medium))

    BetterNeptunTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = LocalizationKey.LOGIN_INPUT_PASSWORD.localized(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onLoginClick() }),
        trailingIcon = {
            IconButton(onClick = onPasswordVisibilityToggle) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = BetterNeptunTheme.colorScheme.primary
                )
            }
        }
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
            onCheckedChange = onStayLoggedInChange,
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
}

@Composable
private fun BetterNeptunTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = trailingIcon,
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
}

@Composable
private fun LoginSubmitButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.6f),
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

@PreviewLightDark
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun LoginContentPreview() {
    LoginContent(
        isLandscape = false,
        isButtonEnabled = true,
        isLoading = false,
        onLoginClick = {},
        onNeptunCodeChange = {},
        onPasswordChange = {},
        onKeepMeLoggedInChange = {}
    )
}

@Preview(
    name = "Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
)
@PreviewWrapper(PreviewThemeProvider::class)
@Composable
fun LoginContentLandscapePreview() {
    LoginContent(
        isLandscape = true,
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
        isLandscape = false,
        isButtonEnabled = false,
        isLoading = true,
        onLoginClick = {},
        onNeptunCodeChange = {},
        onPasswordChange = {},
        onKeepMeLoggedInChange = {}
    )
}
