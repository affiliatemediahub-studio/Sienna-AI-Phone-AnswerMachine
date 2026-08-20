package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SiennaPrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = SiennaPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = SiennaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color.White,
    tertiary = SiennaAccent,
    onTertiary = Color.Black,
    background = BentoBackground,
    onBackground = TextPrimary,
    surface = BentoSurface,
    onSurface = TextPrimary,
    surfaceVariant = BentoSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BentoCardBorder,
    outlineVariant = Color(0xFF1E293B),
    error = SentimentNegative,
    onError = Color.White
)

private val LightColorScheme = DarkColorScheme // Defaulting to sleek dark bento theme for consistent AI screening visual identity

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
