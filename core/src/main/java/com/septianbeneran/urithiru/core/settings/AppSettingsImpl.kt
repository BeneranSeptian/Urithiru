package com.septianbeneran.urithiru.core.settings

import com.septianbeneran.urithiru.core.util.datastore.BaseDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppSettingsImpl @Inject constructor(
    private val baseDataStore: BaseDataStore
) : AppSettings {
    override val themeMode: Flow<ThemeMode?> = baseDataStore.readString(THEME_MODE_KEY).map { name ->
        ThemeMode.entries.firstOrNull { it.name == name }
    }

    override val hasCompletedOnboarding: Flow<Boolean> =
        baseDataStore.readBoolean(ONBOARDING_COMPLETED_KEY).map { it ?: false }

    override suspend fun saveThemeMode(themeMode: ThemeMode) {
        baseDataStore.saveString(THEME_MODE_KEY, themeMode.name)
    }

    override suspend fun setOnboardingCompleted() {
        baseDataStore.saveBoolean(ONBOARDING_COMPLETED_KEY, true)
    }

    private companion object {
        const val THEME_MODE_KEY = "THEME_MODE"
        const val ONBOARDING_COMPLETED_KEY = "ONBOARDING_COMPLETED"
    }
}
