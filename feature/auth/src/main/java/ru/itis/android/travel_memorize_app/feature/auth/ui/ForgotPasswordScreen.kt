package ru.itis.android.travel_memorize_app.feature.auth.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itis.android.travel_memorize_app.core.ui.components.CustomButton
import ru.itis.android.travel_memorize_app.core.ui.components.CustomTextField
import ru.itis.android.travel_memorize_app.core.ui.components.ErrorMessage
import ru.itis.android.travel_memorize_app.core.ui.extensions.clearFocusOnTap
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.ForgotPasswordViewModel
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBack: () -> Unit,
    onBackToSignIn: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val resetSentMessage = stringResource(R.string.toast_reset_sent)

    LaunchedEffect(viewModel.sent) {
        if (viewModel.sent) {
            Toast.makeText(context, resetSentMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF9F4))
            .clearFocusOnTap()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 32.dp, vertical = 20.dp)
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
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 45.sp,
            color = Color(0xFF1C1C19)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.forgot_subtitle),
            color = Color(0xCC414845),
            fontSize = 16.sp,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.label_email).uppercase(),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 1.2.sp,
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
                imeAction = ImeAction.Done,
                autoCorrectEnabled = false
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (viewModel.canSubmit()) viewModel.sendResetLink()
                }
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

        viewModel.commonError?.let {
            Spacer(modifier = Modifier.height(6.dp))
            ErrorMessage(it)
        }

        Spacer(modifier = Modifier.height(28.dp))

        CustomButton(
            text = stringResource(R.string.button_send_reset_link),
            onClick = {
                focusManager.clearFocus()
                viewModel.sendResetLink()
            },
            enabled = viewModel.canSubmit(),
            modifier = Modifier.fillMaxWidth(),
            height = 56.dp,
            endIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send_plane),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.forgot_back_to_sign_in),
            color = Color(0xFF43664D),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onBackToSignIn() }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}