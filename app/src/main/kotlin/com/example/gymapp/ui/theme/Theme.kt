package com.example.gymapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFD4A5A5),       // Soft rose
    secondary = Color(0xFFF3E1E1),     // Very light rose
    tertiary = Color(0xFF9E7777),      // Darker rose
    background = Color(0xFFFAF3F3),    // Off-white with rose tint
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF1D1B1B),     // Almost black
    onSecondary = Color(0xFF1D1B1B),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1D1B1B),
    onSurface = Color(0xFF1D1B1B)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD4A5A5),       // Same soft rose
    secondary = Color(0xFF9E7777),     // Darker rose
    tertiary = Color(0xFFF3E1E1),      // Light rose
    background = Color(0xFF1D1B1B),    // Dark background
    surface = Color(0xFF2D2B2B),       // Slightly lighter dark
    onPrimary = Color(0xFF1D1B1B),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFF1D1B1B),
    onBackground = Color(0xFFF3E1E1),
    onSurface = Color(0xFFF3E1E1)
)

@Composable
fun GymAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
} 