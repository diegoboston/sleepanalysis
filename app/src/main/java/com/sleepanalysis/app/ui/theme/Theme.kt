package com.sleepanalysis.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SleepColors = darkColorScheme(
    primary = Color(0xFFC9B8FF),
    onPrimary = Color(0xFF2A2148),
    secondary = Color(0xFF9AD7FF),
    onSecondary = Color(0xFF00344A),
    background = Color(0xFF12101C),
    onBackground = Color(0xFFF4EFFF),
    surface = Color(0xFF1A1633),
    onSurface = Color(0xFFF4EFFF),
    surfaceVariant = Color(0xFF2A2548),
    onSurfaceVariant = Color(0xFFC8C0E0),
)

@Composable
fun SleepAnalysisTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SleepColors.background.toArgb()
            window.navigationBarColor = SleepColors.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }
    MaterialTheme(colorScheme = SleepColors, content = content)
}
