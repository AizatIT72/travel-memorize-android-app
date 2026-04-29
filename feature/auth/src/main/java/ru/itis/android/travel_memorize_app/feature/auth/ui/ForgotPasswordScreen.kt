package ru.itis.android.travel_memorize_app.feature.auth.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.itis.android.travel_memorize_app.core.ui.components.BackButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomCard
import ru.itis.android.travel_memorize_app.core.ui.components.CustomTextField
import ru.itis.android.travel_memorize_app.core.ui.components.ErrorMessage
import ru.itis.android.travel_memorize_app.core.ui.extensions.clearFocusOnTap
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.AuthEffect
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.ForgotPasswordViewModel
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBack: () -> Unit,
    onBackToSignIn: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val resetSentMessage = stringResource(R.string.toast_reset_sent)
    val colorScheme = MaterialTheme.colorScheme
    val extendedColors = LocalExtendedColors.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AuthEffect.PasswordResetSent -> {
                    Toast.makeText(context, resetSentMessage, Toast.LENGTH_SHORT).show()
                }
                AuthEffect.NavigateToMap -> Unit
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
            .padding(horizontal = 32.dp, vertical = 20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            BackButton(onClick = onBack)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.img_forgot_top),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(28.dp))
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.forgot_title),
            style = MaterialTheme.typography.displayLarge.copy(lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified),
            color = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.forgot_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                lineHeight = 26.sp
            ),
            color = extendedColors.secondaryText
        )
        Spacer(modifier = Modifier.height(28.dp))
        CustomCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
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
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = false
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.sendResetLink()
                        }
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

                state.commonError?.let { error ->
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorMessage(stringResource(error.toMessageRes()))                }

                Spacer(modifier = Modifier.height(28.dp))

                CustomButton(
                    text = stringResource(R.string.button_send_reset_link),
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.sendResetLink()
                    },
                    enabled = state.isSubmitEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    height = 56.dp,
                    endIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_send_plane),
                            contentDescription = null,
                            tint = colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.forgot_back_to_sign_in),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            ),
            color = extendedColors.linkText,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onBackToSignIn() }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}