package com.septianbeneran.urithiru.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight100
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight300
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight500
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Dark.Dark200
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Dark.Dark300
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Dark.Dark400
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Dark.Dark500
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Light.Light100
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Light.Light200
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Light.Light300
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Light.Light400
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Light.Light500
import com.septianbeneran.urithiru.core.ui.theme.Support.Error.Error100
import com.septianbeneran.urithiru.core.ui.theme.Support.Error.Error300
import com.septianbeneran.urithiru.core.ui.theme.Support.Error.Error500
import com.septianbeneran.urithiru.core.ui.theme.Support.Success.Success100
import com.septianbeneran.urithiru.core.ui.theme.Support.Success.Success300
import com.septianbeneran.urithiru.core.ui.theme.Support.Success.Success500

private val LightColorScheme = lightColorScheme(
    primary = Highlight500,
    onPrimary = Light100,
    primaryContainer = Highlight100,
    onPrimaryContainer = Highlight500,
    inversePrimary = Highlight300,

    secondary = Dark300,
    onSecondary = Light100,
    secondaryContainer = Light300,
    onSecondaryContainer = Dark500,

    tertiary = Success500,
    onTertiary = Light100,
    tertiaryContainer = Success100,
    onTertiaryContainer = Success500,

    background = Light200,
    onBackground = Dark500,

    surface = Light100,
    onSurface = Dark500,
    surfaceVariant = Light300,
    onSurfaceVariant = Dark200,
    surfaceTint = Color.Transparent,
    inverseSurface = Dark500,
    inverseOnSurface = Light200,

    surfaceDim = Light300,
    surfaceBright = Light100,
    surfaceContainerLowest = Light100,
    surfaceContainerLow = Light200,
    surfaceContainer = Light200,
    surfaceContainerHigh = Light300,
    surfaceContainerHighest = Light300,

    error = Error500,
    onError = Light100,
    errorContainer = Error100,
    onErrorContainer = Error500,

    outline = Light500,
    outlineVariant = Light400,
    scrim = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Highlight300,
    onPrimary = Dark500,
    primaryContainer = Highlight500,
    onPrimaryContainer = Highlight100,
    inversePrimary = Highlight500,

    secondary = Light400,
    onSecondary = Dark500,
    secondaryContainer = Dark300,
    onSecondaryContainer = Light200,

    tertiary = Success300,
    onTertiary = Dark500,
    tertiaryContainer = Success500,
    onTertiaryContainer = Success100,

    background = Dark500,
    onBackground = Light200,

    surface = Dark400,
    onSurface = Light200,
    surfaceVariant = Dark300,
    onSurfaceVariant = Light400,
    surfaceTint = Color.Transparent,
    inverseSurface = Light200,
    inverseOnSurface = Dark500,

    surfaceDim = Dark500,
    surfaceBright = Dark300,
    surfaceContainerLowest = Dark500,
    surfaceContainerLow = Dark400,
    surfaceContainer = Dark400,
    surfaceContainerHigh = Dark300,
    surfaceContainerHighest = Dark300,

    error = Error300,
    onError = Dark500,
    errorContainer = Error500,
    onErrorContainer = Error100,

    outline = Dark200,
    outlineVariant = Dark300,
    scrim = Color.Black
)

val LocalUrithiruTypography = staticCompositionLocalOf { UrithiruTypographyValues }

@Composable
fun UrithiruTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    CompositionLocalProvider(
        LocalUrithiruTypography provides UrithiruTypographyValues
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MaterialUrithiruTypography,
            content = content
        )
    }
}

object UrithiruTheme {
    val typography: UrithiruTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalUrithiruTypography.current
}