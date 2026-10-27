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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.septianbeneran.urithiru.core.settings.AppSettings
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.theme.applyToSystem
import com.septianbeneran.urithiru.core.ui.theme.isDarkTheme
import com.septianbeneran.urithiru.core.ui.theme.withThemeMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appSettings: AppSettings

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withThemeMode(UrithiruProject.currentThemeMode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        observeThemeMode()

        setContent {
            val darkTheme = UrithiruProject.currentThemeMode.isDarkTheme()

            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
                )
                onDispose {}
            }

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

    /**
     * Recreates the activity when the saved theme changes, the same way AppCompat applies a new
     * night mode, so resources, the window and the system bars all pick it up.
     */
    private fun observeThemeMode() {
        lifecycleScope.launch {
            appSettings.themeMode
                .map { it ?: UrithiruProject.defaultThemeMode }
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
}
