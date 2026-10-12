package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NepalCrimsonDark,
    onPrimary = DarkBackground,
    primaryContainer = NepalDeepMaroon,
    onPrimaryContainer = TextPrimaryDark,
    secondary = HimalayanBlueLight,
    onSecondary = DarkBackground,
    secondaryContainer = HimalayanBlue,
    onSecondaryContainer = TextPrimaryDark,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderSubtleDark
)

private val LightColorScheme = lightColorScheme(
    primary = NepalCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEE2E2), // Soft Crimson tint
    onPrimaryContainer = NepalDeepMaroon,
    secondary = HimalayanBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE), // Soft Blue tint
    onSecondaryContainer = HimalayanBlue,
    background = Color(0xFFF8F9FA), // Clean crisp warm light background
    onBackground = Color(0xFF1A1A1A), // Deep black-slate high-contrast text
    surface = Color(0xFFFFFFFF), // Pure white paper card surface
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = Color(0xFF5F6368),
    outline = Color(0xFFE5E7EB)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to clean light theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
