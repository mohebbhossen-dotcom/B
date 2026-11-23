package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BatteryGreen,
    onPrimary = Color.Black,
    primaryContainer = BatteryGreenContainer,
    onPrimaryContainer = Color.White,
    secondary = BatteryChargingCyan,
    onSecondary = Color.Black,
    tertiary = BatteryOrange,
    onTertiary = Color.Black,
    background = AmoledBlack,
    onBackground = DarkTextPrimary,
    surface = AmoledSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = AmoledBorder,
    error = BatteryRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BatteryGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFF0891B2),
    onSecondary = Color.White,
    tertiary = BatteryOrange,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = BatteryRed,
    onError = Color.White
)

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

@Composable
fun BatteryAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
