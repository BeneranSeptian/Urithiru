package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.localization.localizedString
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

/** Light / Dark / Match system choice. Save the picked mode with `AppSettings.saveThemeMode`. */
@Composable
fun UriThemePicker(
    modifier: Modifier = Modifier,
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UriChoiceOption(
            title = localizedString(R.string.theme_light),
            description = localizedString(R.string.theme_light_description),
            isSelected = selected == ThemeMode.Light
        ) { onSelect(ThemeMode.Light) }
        UriChoiceOption(
            title = localizedString(R.string.theme_dark),
            description = localizedString(R.string.theme_dark_description),
            isSelected = selected == ThemeMode.Dark
        ) { onSelect(ThemeMode.Dark) }
        UriChoiceOption(
            title = localizedString(R.string.theme_system),
            description = localizedString(R.string.theme_system_description),
            isSelected = selected == ThemeMode.System
        ) { onSelect(ThemeMode.System) }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriThemePickerPrev() {
    UrithiruTheme {
        UriThemePicker(selected = ThemeMode.System, onSelect = {})
    }
}
