package com.example.marchify.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Light Color Scheme for MarchiFy
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors (Green)
    primary = PrimaryGreen,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = PrimaryGreenDark,

    // Secondary colors (Orange)
    secondary = AccentOrange,
    onSecondary = TextOnPrimary,
    secondaryContainer = AccentOrangeLight,
    onSecondaryContainer = AccentOrangeDark,

    // Tertiary (for additional accents)
    tertiary = Success,
    onTertiary = TextOnPrimary,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),

    // Background
    background = BackgroundGray,
    onBackground = TextPrimary,

    // Surface (cards, sheets)
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = TextSecondary,

    // Error
    error = Error,
    onError = TextOnPrimary,
    errorContainer = ErrorBackground,
    onErrorContainer = Error,

    // Outline (borders)
    outline = BorderMedium,
    outlineVariant = BorderLight,

    // Other
    scrim = OverlayDark,
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundWhite,
    inversePrimary = PrimaryGreenLight,
    surfaceTint = PrimaryGreen
)

/**
 * Dark Color Scheme for MarchiFy
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = DarkPrimary,
    onPrimary = DarkBackground,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = PrimaryGreenLight,

    // Secondary colors
    secondary = AccentOrange,
    onSecondary = DarkBackground,
    secondaryContainer = AccentOrangeDark,
    onSecondaryContainer = AccentOrangeLight,

    // Tertiary
    tertiary = Success,
    onTertiary = DarkBackground,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFF6EE7B7),

    // Background
    background = DarkBackground,
    onBackground = DarkOnBackground,

    // Surface
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = Color(0xFFD1D5DB),

    // Error
    error = Color(0xFFFF6B6B),
    onError = DarkBackground,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),

    // Outline
    outline = Color(0xFF4B5563),
    outlineVariant = Color(0xFF374151),

    // Other
    scrim = Color(0xCC000000),
    inverseSurface = BackgroundWhite,
    inverseOnSurface = TextPrimary,
    inversePrimary = PrimaryGreen,
    surfaceTint = DarkPrimary
)

/**
 * MarchiFy Theme
 *
 * @param darkTheme Whether to use dark theme
 * @param dynamicColor Whether to use dynamic colors (Android 12+)
 * @param content Composable content
 */
@Composable
fun MarchifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled by default to use custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Note: Dynamic colors disabled to maintain brand colors
        // dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        //     val context = LocalContext.current
        //     if (darkTheme) dynamicDarkColorScheme(context)
        //     else dynamicLightColorScheme(context)
        // }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Update system bars
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Preview version of theme (always light mode)
 */
@Composable
fun MarchifyThemePreview(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
