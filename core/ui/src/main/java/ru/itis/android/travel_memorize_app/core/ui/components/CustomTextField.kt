package ru.itis.android.travel_memorize_app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: (@Composable (() -> Unit))? = null,
    singleLine: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val extendedColors = LocalExtendedColors.current

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal
                )
            )
        },
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        shape = RoundedCornerShape(999.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,

            focusedContainerColor = extendedColors.inputContainer,
            unfocusedContainerColor = extendedColors.inputContainer,
            errorContainerColor = extendedColors.inputContainer,
            disabledContainerColor = extendedColors.inputContainer,

            cursorColor = colorScheme.primary,

            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface,
            errorTextColor = colorScheme.onSurface,

            focusedPlaceholderColor = extendedColors.inputMuted,
            unfocusedPlaceholderColor = extendedColors.inputMuted,
            errorPlaceholderColor = extendedColors.inputMuted,

            focusedTrailingIconColor = extendedColors.inputMuted,
            unfocusedTrailingIconColor = extendedColors.inputMuted,
            errorTrailingIconColor = extendedColors.inputMuted
        )
    )
}