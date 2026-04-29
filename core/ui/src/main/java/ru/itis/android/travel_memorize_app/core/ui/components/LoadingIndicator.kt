package ru.itis.android.travel_memorize_app.core.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun LoadingIndicator() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.primary
    )
}