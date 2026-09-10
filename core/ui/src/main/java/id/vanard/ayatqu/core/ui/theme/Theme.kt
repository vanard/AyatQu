package id.vanard.ayatqu.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = AyatQuDarkColors.primary,
    onPrimary = AyatQuDarkColors.onPrimary,
    primaryContainer = AyatQuDarkColors.surfaceVariant,
    onPrimaryContainer = AyatQuDarkColors.textPrimary,
    secondary = AyatQuDarkColors.accentGold,
    onSecondary = AyatQuDarkColors.background,
    background = AyatQuDarkColors.background,
    onBackground = AyatQuDarkColors.textPrimary,
    surface = AyatQuDarkColors.surface,
    onSurface = AyatQuDarkColors.textPrimary,
    surfaceVariant = AyatQuDarkColors.surfaceVariant,
    onSurfaceVariant = AyatQuDarkColors.textSecondary,
    outline = AyatQuDarkColors.border,
    outlineVariant = AyatQuDarkColors.divider,
    error = AyatQuDarkColors.error,
)

private val LightColorScheme = lightColorScheme(
    primary = AyatQuLightColors.primary,
    onPrimary = AyatQuLightColors.onPrimary,
    primaryContainer = AyatQuLightColors.surfaceVariant,
    onPrimaryContainer = AyatQuLightColors.textPrimary,
    secondary = AyatQuLightColors.accentGold,
    onSecondary = AyatQuLightColors.background,
    background = AyatQuLightColors.background,
    onBackground = AyatQuLightColors.textPrimary,
    surface = AyatQuLightColors.surface,
    onSurface = AyatQuLightColors.textPrimary,
    surfaceVariant = AyatQuLightColors.surfaceVariant,
    onSurfaceVariant = AyatQuLightColors.textSecondary,
    outline = AyatQuLightColors.border,
    outlineVariant = AyatQuLightColors.divider,
    error = AyatQuLightColors.error,
)

@Composable
fun AyatQuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) AyatQuDarkColors else AyatQuLightColors

    CompositionLocalProvider(LocalAyatQuColors provides colors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content,
        )
    }
}

object AyatQuTheme {
    val colors: AyatQuColors
        @Composable get() = LocalAyatQuColors.current
}
