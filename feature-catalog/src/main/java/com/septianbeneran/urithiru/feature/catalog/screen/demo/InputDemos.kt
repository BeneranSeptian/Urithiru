package com.septianbeneran.urithiru.feature.catalog.screen.demo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
import com.septianbeneran.urithiru.core.ui.component.UriCheckBox
import com.septianbeneran.urithiru.core.ui.component.UriCheckBoxProperties
import com.septianbeneran.urithiru.core.ui.component.UriRadioButton
import com.septianbeneran.urithiru.core.ui.component.UriRadioButtonProperties
import com.septianbeneran.urithiru.core.ui.component.UriSearchBar
import com.septianbeneran.urithiru.core.ui.component.UriTextField
import com.septianbeneran.urithiru.core.ui.component.UriThemePicker
import com.septianbeneran.urithiru.core.ui.localization.localizedString
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.feature.catalog.R

@Composable
fun TextFieldDemo() {
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    DemoSection(localizedString(R.string.catalog_demo_text)) {
        UriTextField(
            value = name,
            onValueChange = { name = it },
            label = localizedString(R.string.catalog_demo_name),
            placeholder = localizedString(R.string.catalog_demo_type_something),
            errorText = if (name.length > 12) localizedString(R.string.catalog_demo_name_too_long) else null
        )
    }
    DemoSection(localizedString(CoreUiR.string.common_password)) {
        UriTextField(value = password, onValueChange = { password = it }, label = localizedString(CoreUiR.string.common_password), isPassword = true)
    }
    DemoSection(localizedString(R.string.catalog_demo_disabled)) {
        UriTextField(value = localizedString(R.string.catalog_demo_read_only), onValueChange = {}, label = localizedString(CoreUiR.string.common_username), enabled = false)
    }
}

@Composable
fun SearchBarDemo() {
    var query by rememberSaveable { mutableStateOf("") }
    var lastSearch by rememberSaveable { mutableStateOf<String?>(null) }

    DemoSection(localizedString(R.string.catalog_demo_try_it)) {
        UriSearchBar(
            value = query,
            onValueChange = { query = it },
            onSearch = { lastSearch = query },
            placeholder = localizedString(R.string.catalog_demo_search_weapon),
            onClear = { lastSearch = null }
        )
        Text(
            text = lastSearch?.let { localizedString(R.string.catalog_demo_searched_for, it) }
                ?: localizedString(R.string.catalog_demo_press_search),
            style = UrithiruTheme.typography.bodyS,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CheckBoxDemo() {
    var small by rememberSaveable { mutableStateOf(true) }
    var medium by rememberSaveable { mutableStateOf(false) }
    var large by rememberSaveable { mutableStateOf(true) }

    DemoSection(localizedString(R.string.catalog_demo_sizes)) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            UriCheckBox(checked = small, size = UriCheckBoxProperties.Size.Small, onChecked = { small = it })
            UriCheckBox(checked = medium, size = UriCheckBoxProperties.Size.Medium, onChecked = { medium = it })
            UriCheckBox(checked = large, size = UriCheckBoxProperties.Size.Large, onChecked = { large = it })
        }
    }
}

@Composable
fun RadioButtonDemo() {
    val options = listOf(CoreUiR.string.theme_light, CoreUiR.string.theme_dark, CoreUiR.string.theme_system)
    var selected by rememberSaveable { mutableStateOf(options.first()) }

    DemoSection(localizedString(R.string.catalog_demo_group)) {
        options.forEach { option ->
            Row(
                modifier = Modifier.clickable { selected = option },
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UriRadioButton(selected = selected == option, onSelected = { selected = option })
                Text(
                    text = localizedString(option),
                    style = UrithiruTheme.typography.bodyM,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
    DemoSection(localizedString(R.string.catalog_demo_sizes_and_disabled)) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            UriRadioButton(selected = true, size = UriRadioButtonProperties.Size.Small)
            UriRadioButton(selected = true, size = UriRadioButtonProperties.Size.Medium)
            UriRadioButton(selected = true, size = UriRadioButtonProperties.Size.Large)
            UriRadioButton(selected = false, enabled = false)
        }
    }
}

@Composable
fun ThemePickerDemo() {
    var selected by rememberSaveable { mutableStateOf(ThemeMode.System) }

    DemoSection(localizedString(R.string.catalog_demo_try_it)) {
        UriThemePicker(selected = selected, onSelect = { selected = it })
        Text(
            text = localizedString(R.string.catalog_demo_theme_note),
            style = UrithiruTheme.typography.bodyS,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
