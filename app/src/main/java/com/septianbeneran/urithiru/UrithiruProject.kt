package com.septianbeneran.urithiru

import android.app.Application
import com.septianbeneran.urithiru.core.settings.AppSettings
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.theme.applyToSystem
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
        currentThemeMode = runBlocking { appSettings.themeMode.first() } ?: defaultThemeMode
        currentThemeMode.applyToSystem(this)
    }

    companion object {
        val defaultThemeMode = ThemeMode.System

        var currentThemeMode = defaultThemeMode
            internal set
    }
}
