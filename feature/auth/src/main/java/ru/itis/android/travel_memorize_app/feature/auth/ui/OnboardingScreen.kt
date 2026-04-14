package ru.itis.android.travel_memorize_app.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itis.android.travel_memorize_app.core.ui.components.CustomButton
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF9F4))
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_onboarding_star_placeholder),
            contentDescription = null,
            modifier = Modifier.size(33.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(id = R.string.onboarding_title),
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1C1C19),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(26.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(326f / 407.5f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_onboarding_main),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(32.dp))
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x33FCF9F4),
                                Color(0x66FCF9F4)
                            )
                        )
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.img_onboarding_thumb),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 12.dp)
                    .size(120.dp)
                    .rotate(3f)
            )
        }

        Spacer(modifier = Modifier.height(78.dp))

        Text(
            text = stringResource(id = R.string.onboarding_subtitle).uppercase(),
            color = Color(0x99163429),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.4.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.onboarding_description),
            color = Color(0xFF414845),
            fontSize = 18.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.86f)
        )

        Spacer(modifier = Modifier.weight(1f))

        CustomButton(
            text = stringResource(id = R.string.button_get_started),
            onClick = onGetStarted,
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            height = 68.dp
        )

        Spacer(modifier = Modifier.height(18.dp))
    }
}