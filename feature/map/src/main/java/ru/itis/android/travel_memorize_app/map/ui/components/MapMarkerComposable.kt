package ru.itis.android.travel_memorize_app.map.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors

@Composable
fun MapMarkerComposable(
    title: String?,
    photoUrl: String?,
    modifier: Modifier = Modifier
) {
    val colors = LocalExtendedColors.current
    Column(
        modifier = modifier.size(width = 52.dp, height = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(10.dp, CircleShape)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(colors.inputMuted.copy(alpha = 0.45f))
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(photoUrl),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )

            }
        }
        Box(
            modifier = Modifier
                .offset(y = (-4).dp)
                .width(4.dp)
                .height(12.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )

    }
}