package com.septianbeneran.urithiru.core.ui.theme

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.septianbeneran.urithiru.core.settings.ThemeMode

@Composable
fun ThemeMode.isDarkTheme(): Boolean = when (this) {
    ThemeMode.System -> isSystemInDarkTheme()
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
}

/**
 * Tells Android 12+ which mode the app uses, so the launch window Android draws before the app's
 * code runs matches it. The system remembers this, so it applies from the next launch on.
 */
fun ThemeMode.applyToSystem(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

    context.getSystemService(UiModeManager::class.java)?.setApplicationNightMode(
        when (this) {
            ThemeMode.System -> UiModeManager.MODE_NIGHT_AUTO
            ThemeMode.Light -> UiModeManager.MODE_NIGHT_NO
            ThemeMode.Dark -> UiModeManager.MODE_NIGHT_YES
        }
    )
}

/**
 * Returns a context whose resources use [mode], so an activity's window background and `values-night`
 * resources match it on every Android version. Use it in `Activity.attachBaseContext`.
 */
fun Context.withThemeMode(mode: ThemeMode): Context {
    val nightFlag = when (mode) {
        ThemeMode.System -> return this
        ThemeMode.Light -> Configuration.UI_MODE_NIGHT_NO
        ThemeMode.Dark -> Configuration.UI_MODE_NIGHT_YES
    }

    val configuration = Configuration(resources.configuration).apply {
        uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or nightFlag
    }
    return createConfigurationContext(configuration)
}
