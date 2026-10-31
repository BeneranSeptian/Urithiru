package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.settings.ThemeMode
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
        ThemeOption("Light", "Always use the light theme", selected == ThemeMode.Light) { onSelect(ThemeMode.Light) }
        ThemeOption("Dark", "Always use the dark theme", selected == ThemeMode.Dark) { onSelect(ThemeMode.Dark) }
        ThemeOption("Match system", "Follow your phone's setting", selected == ThemeMode.System) {
            onSelect(ThemeMode.System)
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UriRadioButton(selected = isSelected, onSelected = { onClick() })
            Column {
                Text(
                    text = title,
                    style = UrithiruTheme.typography.h4,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = UrithiruTheme.typography.bodyS,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriThemePickerPrev() {
    UrithiruTheme {
        UriThemePicker(selected = ThemeMode.System, onSelect = {})
    }
}
