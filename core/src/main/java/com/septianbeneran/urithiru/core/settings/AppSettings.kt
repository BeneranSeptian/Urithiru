package com.septianbeneran.urithiru.core.settings

import kotlinx.coroutines.flow.Flow

/** App-wide preferences that aren't tied to one API, saved on the device. */
interface AppSettings {
    val themeMode: Flow<ThemeMode?>

    /** The saved [themeMode], or the app's default until the user picks one. Use this to show the current choice. */
    val effectiveThemeMode: Flow<ThemeMode>

    /** The language tag the user picked (e.g. `id`), or null until they pick one. */
    val language: Flow<String?>

    /**
     * The language in use: the saved one while the translations still have it, otherwise the translation
     * file's default (English). The device language is never used.
     */
    val effectiveLanguage: Flow<String>

    val hasCompletedOnboarding: Flow<Boolean>

    suspend fun saveThemeMode(themeMode: ThemeMode)
    suspend fun saveLanguage(languageTag: String)
    suspend fun setOnboardingCompleted()
}
