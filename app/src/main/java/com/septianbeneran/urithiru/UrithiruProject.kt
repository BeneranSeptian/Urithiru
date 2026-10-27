package com.septianbeneran.urithiru

import android.app.Application
import com.septianbeneran.urithiru.core.ui.theme.ThemeMode
import com.septianbeneran.urithiru.core.ui.theme.applyToSystem
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UrithiruProject: Application() {

    override fun onCreate() {
        super.onCreate()
        themeMode.applyToSystem(this)
    }

    companion object {
        val themeMode = ThemeMode.System
    }
}
