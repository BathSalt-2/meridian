package ai.or4cl3.meridian.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MeridianLightColorScheme = lightColorScheme(
    primary = MeridianGreen,
    onPrimary = MeridianSurface,
    primaryContainer = MeridianGreenContainer,
    onPrimaryContainer = MeridianBrown,
    secondary = MeridianBrown,
    onSecondary = MeridianSurface,
    tertiary = MeridianSky,
    background = MeridianBackground,
    surface = MeridianSurface,
    onSurface = MeridianOnSurface,
    surfaceVariant = MeridianSurfaceVariant,
    error = MeridianRed
)

private val MeridianDarkColorScheme = darkColorScheme(
    primary = MeridianGreenDark,
    onPrimary = MeridianBackgroundDark,
    secondary = MeridianBrownDark,
    background = MeridianBackgroundDark,
    surface = MeridianSurfaceDark,
    onSurface = MeridianGreenContainer
)

@Composable
fun MeridianTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MeridianDarkColorScheme else MeridianLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MeridianTypography,
        content = content
    )
}
