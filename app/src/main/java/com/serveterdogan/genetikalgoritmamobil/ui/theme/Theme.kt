package com.serveterdogan.genetikalgoritmamobil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = TextOnPrimary,
    
    secondary = ModulePurple,
    onSecondary = TextOnPrimary,
    
    tertiary = ModuleOrange,
    onTertiary = TextOnPrimary,
    
    background = BackgroundDark,
    onBackground = TextOnPrimary,
    
    surface = SurfaceDark,
    onSurface = TextOnPrimary,
    
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    
    error = Error,
    onError = TextOnPrimary,
    
    outline = BorderDark,
    outlineVariant = BorderDark.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = TextPrimary,
    
    secondary = ModulePurple,
    onSecondary = TextOnPrimary,
    
    tertiary = ModuleOrange,
    onTertiary = TextOnPrimary,
    
    background = BackgroundLight,
    onBackground = TextPrimary,
    
    surface = SurfaceLight,
    onSurface = TextPrimary,
    
    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondary,
    
    error = Error,
    onError = TextOnPrimary,
    
    outline = BorderLight,
    outlineVariant = BorderLight.copy(alpha = 0.5f)
)

@Composable
fun GenetikAlgoritmaMobilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
