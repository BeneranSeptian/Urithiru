package com.septianbeneran.urithiru.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight100
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight500

import androidx.compose.material3.darkColorScheme
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight300
import com.septianbeneran.urithiru.core.ui.theme.Support.Error.Error100
import com.septianbeneran.urithiru.core.ui.theme.Support.Error.Error300
import com.septianbeneran.urithiru.core.ui.theme.Support.Error.Error500

private val LightColorScheme = lightColorScheme(
    primary = Highlight500,
    onPrimary = Neutral.Light.Light100,
    primaryContainer = Highlight100,
    onPrimaryContainer = Highlight500,

    background = Neutral.Light.Light200,
    surface = Neutral.Light.Light100,

    onBackground = Neutral.Dark.Dark500,
    onSurface = Neutral.Dark.Dark500,
    surfaceVariant = Neutral.Light.Light300,
    onSurfaceVariant = Neutral.Dark.Dark200,

    error = Error500,
    onError = Neutral.Light.Light100,
    errorContainer = Error100,
    onErrorContainer = Error500
)

private val DarkColorScheme = darkColorScheme(
    primary = Highlight300,
    onPrimary = Neutral.Dark.Dark500,
    primaryContainer = Highlight500,
    onPrimaryContainer = Highlight100,

    background = Neutral.Dark.Dark500,
    surface = Neutral.Dark.Dark400,

    onBackground = Neutral.Light.Light200,
    onSurface = Neutral.Light.Light200,
    surfaceVariant = Neutral.Dark.Dark300,
    onSurfaceVariant = Neutral.Light.Light400,

    error = Error300,
    onError = Neutral.Dark.Dark500,
    errorContainer = Error500,
    onErrorContainer = Error100
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