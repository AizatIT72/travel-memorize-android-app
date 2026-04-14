package ru.itis.android.travel_memorize_app.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.itis.android.travel_memorize_app.ui.R

private val AppColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF163429),
    secondary = Color(0xFF2D4B3F),
    background = Color(0xFFFCF9F4),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFBA1A1A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1C19),
    onSurface = Color(0xFF1C1C19),
    onError = Color.White
)

private val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semi_bold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extra_bold, FontWeight.ExtraBold)
)

private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.9).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.75).sp
    ),
    titleLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.4.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 2.4.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.1.sp
    )
)

@Composable
fun TravelMemorizeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = AppTypography,
        content = content
    )
}