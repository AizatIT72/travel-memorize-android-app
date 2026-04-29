package ru.itis.android.travel_memorize_app.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.itis.android.travel_memorize_app.core.ui.components.BackButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomCard
import ru.itis.android.travel_memorize_app.core.ui.components.CustomTextField
import ru.itis.android.travel_memorize_app.core.ui.components.ErrorMessage
import ru.itis.android.travel_memorize_app.core.ui.extensions.clearFocusOnTap
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.AuthEffect
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.SignUpViewModel
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val colorScheme = MaterialTheme.colorScheme
    val extendedColors = LocalExtendedColors.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AuthEffect.NavigateToMap -> onSuccess()
                AuthEffect.PasswordResetSent -> Unit
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .clearFocusOnTap()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            BackButton(onClick = onBack)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(extendedColors.decorativeIconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_leaf_placeholder),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.signup_title),
            style = MaterialTheme.typography.displayLarge,
            color = colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.signup_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal
            ),
            color = extendedColors.secondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))
        CustomCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.label_username).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                CustomTextField(
                    value = state.username,
                    onValueChange = viewModel::onUsernameChanged,
                    placeholder = "explorer_01",
                    isError = state.usernameError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        autoCorrectEnabled = false
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_input_user),
                            contentDescription = null,
                            tint = extendedColors.inputMuted
                        )
                    }
                )
                if (state.usernameError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(R.string.error_invalid_username))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.label_email).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                CustomTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChanged,
                    placeholder = "hello@travelmemorize.com",
                    isError = state.emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        autoCorrectEnabled = false
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_input_email),
                            contentDescription = null,
                            tint = extendedColors.inputMuted
                        )
                    }
                )
                if (state.emailError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(R.string.error_invalid_email))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.label_password).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                CustomTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChanged,
                    placeholder = "••••••••",
                    isError = state.passwordError,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                        autoCorrectEnabled = false
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Filled.VisibilityOff
                                } else {
                                    Icons.Filled.Visibility
                                },
                                contentDescription = null,
                                tint = extendedColors.inputMuted
                            )
                        }
                    }
                )

                if (state.passwordError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(R.string.error_password_length))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.label_confirm_password).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                CustomTextField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChanged,
                    placeholder = "••••••••",
                    isError = state.confirmPasswordError,
                    visualTransformation = if (confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = false
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.signUp()
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) {
                                    Icons.Filled.VisibilityOff
                                } else {
                                    Icons.Filled.Visibility
                                },
                                contentDescription = null,
                                tint = extendedColors.inputMuted
                            )
                        }
                    }
                )
                if (state.confirmPasswordError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(R.string.error_passwords_not_match))
                }
                state.commonError?.let { error ->
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(error.toMessageRes()))
            }

                Spacer(modifier = Modifier.height(24.dp))
                CustomButton(
                    text = stringResource(R.string.button_register),
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.signUp()
                    },
                    enabled = state.isSubmitEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    height = 56.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.signup_have_account),
                style = MaterialTheme.typography.bodyMedium,
                color = extendedColors.bodySecondaryText
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.signup_login_now),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToSignIn() }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}