package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    onPrimary = Slate50,
    primaryContainer = DarkOrange,
    onPrimaryContainer = Slate50,
    secondary = LightOrange,
    onSecondary = Slate900,
    tertiary = BrightGold,
    onTertiary = Slate900,
    background = Slate50,
    onBackground = Slate900,
    surface = CharcoalGray,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    error = CrimsonRed,
    onError = Slate50
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = Slate50,
    primaryContainer = LightOrange,
    onPrimaryContainer = Slate900,
    secondary = DarkOrange,
    onSecondary = Slate50,
    tertiary = BrightGold,
    onTertiary = Slate900,
    background = Slate50,
    onBackground = Slate900,
    surface = CharcoalGray,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    error = CrimsonRed,
    onError = Slate50
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Both Dark and Light theme are mapped to our persuasive Vibrant Light Palette Slate theme!
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            val winCompat = WindowCompat.getInsetsController(window, view)
            winCompat.isAppearanceLightStatusBars = true
            winCompat.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
