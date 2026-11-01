package com.septianbeneran.urithiru.feature.onboarding.screen.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
import com.septianbeneran.urithiru.core.ui.component.UriLanguagePicker
import com.septianbeneran.urithiru.core.ui.component.UriThemePicker
import com.septianbeneran.urithiru.core.ui.localization.localizedString
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
internal fun PreferencesDemo(
    themeMode: ThemeMode,
    language: String,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onLanguageSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PreferenceLabel(localizedString(CoreUiR.string.common_appearance))
        UriThemePicker(selected = themeMode, onSelect = onThemeModeSelect)
        PreferenceLabel(localizedString(CoreUiR.string.common_language))
        UriLanguagePicker(selected = language, onSelect = onLanguageSelect)
    }
}

@Composable
private fun PreferenceLabel(text: String) {
    Text(
        text = text,
        style = UrithiruTheme.typography.h5,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview(showBackground = true)
@Composable
private fun PreferencesDemoPrev() {
    UrithiruTheme {
        PreferencesDemo(
            themeMode = ThemeMode.System,
            language = "en",
            onThemeModeSelect = {},
            onLanguageSelect = {}
        )
    }
}
