package com.septianbeneran.urithiru.core.ui.util.extension

import android.content.Context
import android.content.res.Configuration
import com.septianbeneran.urithiru.core.settings.ThemeMode
import java.util.Locale

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

/**
 * Returns a context whose configuration uses [languageTag] instead of the device language, so Compose's
 * `LocalConfiguration` (and `localizedString`), layout direction and formatting follow the app's choice.
 * Use it in `Activity.attachBaseContext`.
 */
fun Context.withLanguage(languageTag: String): Context {
    val configuration = Configuration(resources.configuration).apply {
        setLocale(Locale.forLanguageTag(languageTag))
    }
    return createConfigurationContext(configuration)
}
