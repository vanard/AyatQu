package id.vanard.ayatqu.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.core.ui.R

val AppFontFamily = FontFamily(
    Font(R.font.plusjakartasans_regular,   FontWeight.Normal),
    Font(R.font.plusjakartasans_medium,    FontWeight.Medium),
    Font(R.font.plusjakartasans_semibold,  FontWeight.SemiBold),
    Font(R.font.plusjakartasans_bold,      FontWeight.Bold),
    Font(R.font.plusjakartasans_extrabold, FontWeight.ExtraBold),
)

val Typography = Typography(
    // "Qurania" brand / screen title — 24sp Bold
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.6).sp
    ),
    // Section heading — 30sp ExtraBold
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 37.5.sp,
        letterSpacing = 0.sp
    ),
    // Form heading — 24sp Bold
    headlineSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // Section title 20sp SemiBold
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    // Signup / sub-title 16sp SemiBold
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    // CTA button label 16sp Bold
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    // Skip / secondary label 14sp Medium
    labelMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    // Input label / meta 12sp Regular
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    // Body / subtitle 14sp Regular
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.75.sp,
        letterSpacing = 0.sp
    ),
    // Legal / hint 12sp Regular
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 19.5.sp,
        letterSpacing = 0.sp
    )
)
