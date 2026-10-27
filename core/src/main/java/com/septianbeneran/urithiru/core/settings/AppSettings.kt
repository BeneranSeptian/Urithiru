package com.septianbeneran.urithiru.core.settings

import kotlinx.coroutines.flow.Flow

/** App-wide preferences that aren't tied to one API, saved on the device. */
interface AppSettings {
    val themeMode: Flow<ThemeMode?>
    val hasCompletedOnboarding: Flow<Boolean>

    suspend fun saveThemeMode(themeMode: ThemeMode)
    suspend fun setOnboardingCompleted()
}
