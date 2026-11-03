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

private val RomanticDarkColorScheme = darkColorScheme(
    primary = RubyCrimson,
    onPrimary = RomanticTextWhite,
    primaryContainer = PlumCardElevated,
    onPrimaryContainer = SoftBlushPink,
    secondary = RoseGold,
    onSecondary = MidnightPlum,
    secondaryContainer = DeepPlumCard,
    onSecondaryContainer = LightChampagne,
    tertiary = ChampagneGold,
    onTertiary = MidnightPlum,
    background = MidnightPlum,
    onBackground = RomanticTextWhite,
    surface = DeepPlumCard,
    onSurface = RomanticTextWhite,
    surfaceVariant = PlumCardElevated,
    onSurfaceVariant = RomanticTextMuted,
    outline = PlumBorder
)

private val RomanticLightColorScheme = lightColorScheme(
    primary = DeepVelvetRose,
    onPrimary = RomanticTextWhite,
    primaryContainer = BlushBorder,
    onPrimaryContainer = TextDarkPlum,
    secondary = RubyCrimson,
    onSecondary = RomanticTextWhite,
    secondaryContainer = SoftBlushPink,
    onSecondaryContainer = TextDarkPlum,
    tertiary = ChampagneGold,
    onTertiary = RomanticTextWhite,
    background = BlushBackground,
    onBackground = TextDarkPlum,
    surface = BlushCard,
    onSurface = TextDarkPlum,
    surfaceVariant = BlushBorder,
    onSurfaceVariant = TextMutedPlum,
    outline = BlushBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to romantic luxury dark theme
    dynamicColor: Boolean = false, // Keep intentional romantic aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RomanticDarkColorScheme else RomanticLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
