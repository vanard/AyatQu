package id.vanard.ayatqu.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = Primary,
    onPrimary        = OnPrimary,
    primaryContainer = PrimaryVariant,
    secondary        = AccentCyan,
    onSecondary      = OnPrimary,
    tertiary         = AccentSky,
    background       = BackgroundDeep,
    onBackground     = OnBackgroundDark,
    surface          = SurfaceNavy,
    onSurface        = OnBackgroundDark,
    surfaceVariant   = SurfaceDeep,
    onSurfaceVariant = TextSecondaryDark,
    outline          = BorderSubtle,
    outlineVariant   = BorderDark,
)

private val LightColorScheme = lightColorScheme(
    primary            = PrimaryOnLight,
    onPrimary          = OnPrimary,
    primaryContainer   = SurfaceVariantLight,
    onPrimaryContainer = PrimaryDark,
    secondary          = AccentCyan,
    onSecondary        = OnPrimary,
    tertiary           = PrimaryVariant,
    background         = BackgroundLight,
    onBackground       = OnBackgroundLight,
    surface            = SurfaceLight,
    onSurface          = OnBackgroundLight,
    surfaceVariant     = SurfaceVariantLight,
    onSurfaceVariant   = TextSecondaryLight,
    outline            = BorderSubtle,
    outlineVariant     = BorderDark,
)

@Composable
fun AyatQuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = Typography,
        content     = content
    )
}
