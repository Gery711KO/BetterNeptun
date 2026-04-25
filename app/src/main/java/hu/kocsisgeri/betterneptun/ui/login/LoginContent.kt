package hu.kocsisgeri.betterneptun.ui.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.kocsisgeri.betterneptun.R

@Composable
fun LoginContent(
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
        containerColor = colorResource(id = R.color.base_fragment_bg),
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(
            WindowInsets(16.dp, 16.dp, 16.dp, 16.dp)
        ),
        bottomBar = {
            if (!isLoading) Button(
                onClick = onLoginClick,
                enabled = isButtonEnabled,
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .imePadding()
                    .fillMaxWidth()
                    .alpha(if (isButtonEnabled) 1f else 0.6f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.base_button_bg),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = "Belépés",
                    fontSize = 18.sp,
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
                    modifier = Modifier.animateBounds(lookaheadScope = this@LookaheadScope)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.oe_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .animateBounds(lookaheadScope = this@LookaheadScope)
                            .size(100.dp)
                            .then(
                                if (!isLoading) Modifier.padding(bottom = 32.dp)
                                else Modifier
                            )

                    )
                    if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.width(100.dp),
                            color = colorResource(id = R.color.base_text_color)
                        )
                    }
                }

                if (!isLoading) {
                    OutlinedTextField(
                        value = neptunCode,
                        onValueChange = {
                            neptunCode = it
                            onNeptunCodeChange(it)
                        },
                        label = { Text("Neptun kód") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = colorResource(id = R.color.text_input_box_color),
                            unfocusedIndicatorColor = colorResource(id = R.color.text_input_box_color),
                            focusedLabelColor = colorResource(id = R.color.text_input_box_color),
                            cursorColor = colorResource(id = R.color.base_text_color),
                            focusedTextColor = colorResource(id = R.color.base_text_color),
                            unfocusedTextColor = colorResource(id = R.color.base_text_color),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        )
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            onPasswordChange(it)
                        },
                        label = { Text("Jelszó") },
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
                                    tint = colorResource(id = R.color.base_text_color)
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = colorResource(id = R.color.text_input_box_color),
                            unfocusedIndicatorColor = colorResource(id = R.color.text_input_box_color),
                            focusedLabelColor = colorResource(id = R.color.text_input_box_color),
                            cursorColor = colorResource(id = R.color.base_text_color),
                            focusedTextColor = colorResource(id = R.color.base_text_color),
                            unfocusedTextColor = colorResource(id = R.color.base_text_color),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Checkbox(
                            checked = stayLoggedIn,
                            onCheckedChange = {
                                stayLoggedIn = it
                                onKeepMeLoggedInChange(it)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = colorResource(id = R.color.base_text_color),
                                uncheckedColor = colorResource(id = R.color.base_text_color)
                            )
                        )
                        Text(
                            text = "Maradjon bejelentkezve?",
                            color = colorResource(id = R.color.base_text_color)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview(showBackground = true)
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

@Preview(showBackground = true)
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
