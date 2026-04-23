package ru.itis.android.travel_memorize_app.core.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.material.progressindicator.CircularProgressIndicator

@Composable
fun LoadingIndicator() {
    CircularProgressIndicator(color = Color(0xFF163429))
}
