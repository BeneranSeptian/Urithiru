package com.septianbeneran.urithiru

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.septianbeneran.urithiru.core.localization.Translator
import com.septianbeneran.urithiru.core.settings.AppSettings
import com.septianbeneran.urithiru.core.ui.localization.LocalTranslator
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.util.extension.applyToSystem
import com.septianbeneran.urithiru.core.ui.util.extension.isDarkTheme
import com.septianbeneran.urithiru.core.ui.util.extension.withLanguage
import com.septianbeneran.urithiru.core.ui.util.extension.withThemeMode
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var translator: Translator

    override fun attachBaseContext(newBase: Context) {
        Locale.setDefault(Locale.forLanguageTag(UrithiruProject.currentLanguage))
        super.attachBaseContext(
            newBase
                .withThemeMode(UrithiruProject.currentThemeMode)
                .withLanguage(UrithiruProject.currentLanguage)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        observeThemeMode()
        observeLanguage()

        setContent {
            val darkTheme = UrithiruProject.currentThemeMode.isDarkTheme()

            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
                )
                onDispose {}
            }

            CompositionLocalProvider(LocalTranslator provides translator) {
                UrithiruTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHostChamber()
                    }
                }
            }
        }
    }

    /**
     * Recreates the activity when the saved theme changes, the same way AppCompat applies a new
     * night mode, so resources, the window and the system bars all pick it up.
     */
    private fun observeThemeMode() {
        lifecycleScope.launch {
            appSettings.effectiveThemeMode
                .distinctUntilChanged()
                .collect { themeMode ->
                    if (themeMode != UrithiruProject.currentThemeMode) {
                        UrithiruProject.currentThemeMode = themeMode
                        themeMode.applyToSystem(this@MainActivity)
                        recreate()
                    }
                }
        }
    }

    /** Recreates the activity when the saved language changes, so every screen is drawn in it. */
    private fun observeLanguage() {
        lifecycleScope.launch {
            appSettings.effectiveLanguage
                .distinctUntilChanged()
                .collect { language ->
                    if (language != UrithiruProject.currentLanguage) {
                        UrithiruProject.currentLanguage = language
                        recreate()
                    }
                }
        }
    }
}
