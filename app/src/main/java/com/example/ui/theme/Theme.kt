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
    primary = Color(0xFFE5B896),
    onPrimary = Color(0xFF3B1A04),
    primaryContainer = Color(0xFF5A2C0D),
    onPrimaryContainer = SoftToffeeSecondary,
    secondary = CaramelSecondary,
    onSecondary = Color(0xFF381F08),
    secondaryContainer = Color(0xFF563518),
    onSecondaryContainer = SoftToffeeContainer,
    tertiary = SoftLavenderDark,
    onTertiary = Color(0xFF38230F),
    tertiaryContainer = Color(0xFF563A21),
    onTertiaryContainer = Color(0xFFF8EDE3),
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextLightSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = CaramelPrimary, // #9E5727
    onPrimary = Color.White,
    primaryContainer = SoftToffeeContainer,
    onPrimaryContainer = CaramelPrimary,
    secondary = CaramelSecondary, // #DDA15E
    onSecondary = TextEspresso,
    secondaryContainer = SoftToffeeContainer,
    onSecondaryContainer = CaramelPrimary,
    tertiary = SoftLavender,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF8EDE3),
    onTertiaryContainer = CaramelPrimary,
    background = WarmSurfaceBackground, // #FDFBF7
    onBackground = TextEspresso,        // #2D1B10
    surface = CrispWhiteSurface,
    onSurface = TextEspresso,           // #2D1B10
    surfaceVariant = SoftToffeeContainer,
    onSurfaceVariant = TextWarmTaupe
)

@Composable
fun MamanBebeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
