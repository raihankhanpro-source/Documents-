package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CivilGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = CivilGreenLight,
    onPrimaryContainer = CivilGreenDark,
    secondary = CivilGreenDark,
    onSecondary = Color.White,
    tertiary = CivilGreenAccent,
    background = CivilBackground,
    onBackground = CivilTextPrimary,
    surface = CivilSurfaceLight,
    onSurface = CivilTextPrimary,
    surfaceVariant = Color(0xFFF1F5F2),
    onSurfaceVariant = CivilTextSecondary,
    outline = CivilBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = CivilGreenAccent,
    onPrimary = Color.White,
    primaryContainer = CivilGreenDark,
    onPrimaryContainer = CivilGreenLight,
    secondary = CivilGreenLight,
    background = Color(0xFF101914),
    onBackground = Color(0xFFE2EDE7),
    surface = Color(0xFF18221D),
    onSurface = Color(0xFFE2EDE7),
    surfaceVariant = Color(0xFF222E28),
    onSurfaceVariant = Color(0xFFBAC7C0),
    outline = Color(0xFF33443C)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
