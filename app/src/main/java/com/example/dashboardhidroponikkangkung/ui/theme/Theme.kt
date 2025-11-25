package com.example.dashboardhidroponikkangkung.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val md_theme_dark_primary = Color(0xFFFFFFFF)
private val md_theme_dark_onPrimary = Color(0xFF000000)
private val md_theme_dark_primaryContainer = Color(0xFF1E1E1E)
private val md_theme_dark_secondary = Color(0xFFB0B0B0)
private val md_theme_dark_onSecondary = Color(0xFF000000)
private val md_theme_dark_background = Color(0xFF0A0A0A)
private val md_theme_dark_onBackground = Color(0xFFFFFFFF)
private val md_theme_dark_surface = Color(0xFF141414)
private val md_theme_dark_onSurface = Color(0xFFFFFFFF)
private val md_theme_dark_surfaceVariant = Color(0xFF1E1E1E)
private val md_theme_dark_outline = Color(0xFF2D2D2D)

// Status Colors
val StatusOptimal = Color(0xFF22C55E)  // Green
val StatusWarning = Color(0xFFF59E0B)  // Orange
val StatusCritical = Color(0xFFEF4444) // Red

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    outline = md_theme_dark_outline
)

@Composable
fun HidroponikTheme(
    darkTheme: Boolean = true, // Force dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}