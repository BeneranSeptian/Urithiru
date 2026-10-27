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
import com.septianbeneran.urithiru.core.ui.component.UriCheckBox
import com.septianbeneran.urithiru.core.ui.component.UriCheckBoxProperties
import com.septianbeneran.urithiru.core.ui.component.UriRadioButton
import com.septianbeneran.urithiru.core.ui.component.UriRadioButtonProperties
import com.septianbeneran.urithiru.core.ui.component.UriSearchBar
import com.septianbeneran.urithiru.core.ui.component.UriTextField
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun TextFieldDemo() {
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    DemoSection("Text") {
        UriTextField(
            value = name,
            onValueChange = { name = it },
            label = "Name",
            placeholder = "Type something",
            errorText = if (name.length > 12) "Use at most 12 characters" else null
        )
    }
    DemoSection("Password") {
        UriTextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)
    }
    DemoSection("Disabled") {
        UriTextField(value = "Read only", onValueChange = {}, label = "Username", enabled = false)
    }
}

@Composable
fun SearchBarDemo() {
    var query by rememberSaveable { mutableStateOf("") }
    var lastSearch by rememberSaveable { mutableStateOf<String?>(null) }

    DemoSection("Try it") {
        UriSearchBar(
            value = query,
            onValueChange = { query = it },
            onSearch = { lastSearch = query },
            placeholder = "Search weapon...",
            onClear = { lastSearch = null }
        )
        Text(
            text = lastSearch?.let { "Searched for \"$it\"" } ?: "Press the keyboard's search key",
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

    DemoSection("Sizes") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            UriCheckBox(checked = small, size = UriCheckBoxProperties.Size.Small, onChecked = { small = it })
            UriCheckBox(checked = medium, size = UriCheckBoxProperties.Size.Medium, onChecked = { medium = it })
            UriCheckBox(checked = large, size = UriCheckBoxProperties.Size.Large, onChecked = { large = it })
        }
    }
}

@Composable
fun RadioButtonDemo() {
    val options = listOf("Light", "Dark", "Match system")
    var selected by rememberSaveable { mutableStateOf(options.first()) }

    DemoSection("Group") {
        options.forEach { option ->
            Row(
                modifier = Modifier.clickable { selected = option },
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UriRadioButton(selected = selected == option, onSelected = { selected = option })
                Text(
                    text = option,
                    style = UrithiruTheme.typography.bodyM,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
    DemoSection("Sizes and disabled") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            UriRadioButton(selected = true, size = UriRadioButtonProperties.Size.Small)
            UriRadioButton(selected = true, size = UriRadioButtonProperties.Size.Medium)
            UriRadioButton(selected = true, size = UriRadioButtonProperties.Size.Large)
            UriRadioButton(selected = false, enabled = false)
        }
    }
}
