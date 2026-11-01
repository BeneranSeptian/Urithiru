package com.septianbeneran.urithiru.core.ui.util.extension

import android.app.UiModeManager
import android.content.Context
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
