package com.septianbeneran.urithiru.core.settings

import kotlinx.coroutines.flow.Flow

/** App-wide preferences that aren't tied to one API, saved on the device. */
interface AppSettings {
    val themeMode: Flow<ThemeMode?>

    /** The saved [themeMode], or the app's default until the user picks one. Use this to show the current choice. */
    val effectiveThemeMode: Flow<ThemeMode>

    val hasCompletedOnboarding: Flow<Boolean>

    suspend fun saveThemeMode(themeMode: ThemeMode)
    suspend fun setOnboardingCompleted()
}
