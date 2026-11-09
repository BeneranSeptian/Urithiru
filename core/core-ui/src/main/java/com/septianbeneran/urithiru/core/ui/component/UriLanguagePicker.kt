package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.localization.availableLanguages
import com.septianbeneran.urithiru.core.ui.localization.languageName
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import java.util.Locale

/**
 * A choice between the languages in the translation file. Each is named in its own language, with its
 * name in the current language below when that differs. Save the pick with `AppSettings.saveLanguage`.
 */
@Composable
fun UriLanguagePicker(
    modifier: Modifier = Modifier,
    selected: String,
    onSelect: (String) -> Unit,
    languages: List<String> = availableLanguages()
) {
    val currentLocale = LocalConfiguration.current.locales[0]

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        languages.forEach { tag ->
            val ownName = languageName(tag)
            val localName = Locale.forLanguageTag(tag).getDisplayName(currentLocale)
            UriChoiceOption(
                title = ownName,
                description = localName.takeUnless { it.equals(ownName, ignoreCase = true) },
                isSelected = tag == selected
            ) { onSelect(tag) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriLanguagePickerPrev() {
    UrithiruTheme {
        UriLanguagePicker(selected = "en", onSelect = {})
    }
}
