package ru.itis.android.travel_memorize_app.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import ru.itis.android.travel_memorize_app.core.ui.components.CustomButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomCard
import ru.itis.android.travel_memorize_app.core.ui.components.CustomTextField
import ru.itis.android.travel_memorize_app.core.ui.components.ErrorMessage
import ru.itis.android.travel_memorize_app.core.ui.extensions.clearFocusOnTap
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.SignUpViewModel
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onSuccess: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.success) {
        if (viewModel.success) onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF9F4))
            .clearFocusOnTap()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_circle_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onBack)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFFE5E2DD), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_leaf_placeholder),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.signup_title),
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            color = Color(0xFF1C1C19),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.signup_subtitle),
            color = Color(0xCC414845),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        CustomCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 24.dp,
                vertical = 28.dp
            )
        ) {
            Column {
                Text(
                    text = stringResource(R.string.label_username).uppercase(),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 1.1.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF163429),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                CustomTextField(
                    value = viewModel.username,
                    onValueChange = viewModel::onUsernameChanged,
                    placeholder = "explorer_01",
                    isError = false,
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
                            tint = Color(0xFFA8A29E)
                        )
                    }
                )

                viewModel.usernameError?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(R.string.error_invalid_username))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.label_email).uppercase(),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 1.1.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF163429),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                CustomTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::onEmailChanged,
                    placeholder = "hello@travelmemorize.com",
                    isError = false,
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
                            tint = Color(0xFFA8A29E)
                        )
                    }
                )

                viewModel.emailError?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(R.string.error_invalid_email))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.label_password).uppercase(),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 1.1.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF163429),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                CustomTextField(
                    value = viewModel.password,
                    onValueChange = viewModel::onPasswordChanged,
                    placeholder = "••••••••",
                    isError = false,
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
                                tint = Color(0xFFA8A29E)
                            )
                        }
                    }
                )

                viewModel.passwordError?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(R.string.error_password_length))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.label_confirm_password).uppercase(),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 1.1.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF163429),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                CustomTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChanged,
                    placeholder = "••••••••",
                    isError = false,
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
                            if (viewModel.canSubmit()) viewModel.signUp()
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
                                tint = Color(0xFFA8A29E)
                            )
                        }
                    }
                )

                viewModel.confirmPasswordError?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(R.string.error_passwords_not_match))
                }

                viewModel.commonError?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(it)
                }

                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    text = stringResource(R.string.button_register),
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.signUp()
                    },
                    enabled = viewModel.canSubmit(),
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
                stringResource(R.string.signup_have_account),
                color = Color(0xFF414845),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                stringResource(R.string.signup_login_now),
                color = Color(0xFF163429),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onNavigateToSignIn() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}