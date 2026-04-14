package ru.itis.android.travel_memorize_app.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
        color = Color(0xFFBA1A1A)
    )
}