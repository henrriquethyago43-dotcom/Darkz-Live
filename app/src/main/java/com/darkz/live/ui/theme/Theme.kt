package com.darkz.live.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkzRedPrimary,
    onPrimary = DarkzWhite,
    primaryContainer = DarkzRedDark,
    onPrimaryContainer = DarkzWhite,
    secondary = DarkzCyanNeon,
    onSecondary = DarkzBlack,
    background = DarkzVoid,
    onBackground = DarkzWhite,
    surface = DarkzSurface,
    onSurface = DarkzWhite,
    surfaceVariant = DarkzSurfaceElevated,
    onSurfaceVariant = DarkzWhiteMuted,
    outline = DarkzBorder
)

@Composable
fun DarkzLiveTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = DarkzVoid.toArgb()
                it.navigationBarColor = DarkzVoid.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = DarkzTypography,
        content = content
    )
}
