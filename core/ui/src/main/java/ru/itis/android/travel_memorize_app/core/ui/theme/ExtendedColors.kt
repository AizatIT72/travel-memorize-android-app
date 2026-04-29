package ru.itis.android.travel_memorize_app.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class ExtendedColors(
    val inputContainer: Color,
    val inputMuted: Color,
    val decorativeIconBackground: Color,
    val secondaryText: Color,
    val bodySecondaryText: Color,
    val linkText: Color
)

val LightExtendedColors = ExtendedColors(
    inputContainer = Color(0xFFEBE8E3),
    inputMuted = Color(0xFFA8A29E),
    decorativeIconBackground = Color(0xFFE5E2DD),
    secondaryText = Color(0xCC414845),
    bodySecondaryText = Color(0xFF414845),
    linkText = Color(0xFF43664D)
)

val DarkExtendedColors = ExtendedColors(
    inputContainer = Color(0xFF1D2823),
    inputMuted = Color(0xFF91A39A),
    decorativeIconBackground = Color(0xFF2A3A31),
    secondaryText = Color(0xCCD7E2DB),
    bodySecondaryText = Color(0xFFE2EAE5),
    linkText = Color(0xFFB7E2C9)
)
val LocalExtendedColors = staticCompositionLocalOf {
    LightExtendedColors
}