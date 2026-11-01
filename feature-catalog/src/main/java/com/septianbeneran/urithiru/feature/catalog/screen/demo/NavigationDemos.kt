package com.septianbeneran.urithiru.feature.catalog.screen.demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBar
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.feature.catalog.R

@Composable
fun TopAppBarDemo() {
    DemoSection(stringResource(R.string.catalog_demo_with_back)) {
        UriTopAppBar(topAppBarArgs = defaults(title = stringResource(CoreUiR.string.common_details)))
    }
    DemoSection(stringResource(R.string.catalog_demo_without_back)) {
        UriTopAppBar(topAppBarArgs = defaults(title = stringResource(CoreUiR.string.common_title_catalog), backIcon = null))
    }
    DemoSection(stringResource(R.string.catalog_demo_with_action)) {
        UriTopAppBar(
            topAppBarArgs = defaults(
                title = stringResource(CoreUiR.string.common_title_profile),
                trailingComposable = {
                    Text(
                        text = stringResource(R.string.catalog_demo_edit),
                        style = UrithiruTheme.typography.actionM,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        )
    }
}
