package ru.itis.android.travel_memorize_app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    endIcon: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(999.dp)

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF163429),
                            Color(0xFF2D4B3F)
                        )
                    )
                )
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            if (endIcon != null) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 6.dp))
                endIcon()
            }
        }
    }
}