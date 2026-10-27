package com.septianbeneran.urithiru.feature.catalog.screen.demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBar
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun TopAppBarDemo() {
    DemoSection("With back button") {
        UriTopAppBar(topAppBarArgs = defaults(title = "Details"))
    }
    DemoSection("Without back button, for tab roots") {
        UriTopAppBar(topAppBarArgs = defaults(title = "Catalog", backIcon = null))
    }
    DemoSection("With an action") {
        UriTopAppBar(
            topAppBarArgs = defaults(
                title = "Profile",
                trailingComposable = {
                    Text(
                        text = "Edit",
                        style = UrithiruTheme.typography.actionM,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        )
    }
}
