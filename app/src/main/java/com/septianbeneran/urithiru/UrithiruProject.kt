package com.septianbeneran.urithiru

import android.app.Application
import com.septianbeneran.urithiru.core.settings.AppSettings
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.util.extension.applyToSystem
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class UrithiruProject : Application() {

    @Inject
    lateinit var appSettings: AppSettings

    override fun onCreate() {
        super.onCreate()
        currentThemeMode = runBlocking { appSettings.effectiveThemeMode.first() }
        currentThemeMode.applyToSystem(this)
        currentLanguage = runBlocking { appSettings.effectiveLanguage.first() }
    }

    companion object {
        val defaultThemeMode = ThemeMode.System

        var currentThemeMode = defaultThemeMode
            internal set

        var currentLanguage = "en"
            internal set
    }
}
