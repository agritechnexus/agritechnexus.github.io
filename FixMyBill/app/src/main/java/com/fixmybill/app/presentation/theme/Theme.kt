package com.fixmybill.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DeepTeal,
    onPrimary = OnPrimaryLight,
    primaryContainer = DeepTealContainer,
    onPrimaryContainer = DeepTealOnContainer,
    secondary = Teal700,
    onSecondary = OnSecondaryLight,
    secondaryContainer = TealContainer,
    onSecondaryContainer = TealOnContainer,
    tertiary = Slate,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = SlateContainer,
    onTertiaryContainer = SlateOnContainer,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedContainer,
    onErrorContainer = ErrorRedOnContainer,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight,
    scrim = Scrim
)

private val DarkColorScheme = darkColorScheme(
    primary = DeepTealLight,
    onPrimary = OnPrimaryDark,
    primaryContainer = DeepTealDark,
    onPrimaryContainer = DeepTealContainer,
    secondary = Teal200,
    onSecondary = OnSecondaryDark,
    secondaryContainer = Teal700,
    onSecondaryContainer = TealContainer,
    tertiary = SlateLight,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = SlateDark,
    onTertiaryContainer = SlateContainer,
    error = ErrorRedDark,
    onError = ErrorRed,
    errorContainer = ErrorRedOnContainer,
    onErrorContainer = ErrorRedLight,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
    scrim = Scrim
)

@Composable
fun FixMyBillTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
