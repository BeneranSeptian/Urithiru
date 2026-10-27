package com.septianbeneran.urithiru.feature.catalog.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_catalog.CatalogComponentRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.feature.catalog.catalog.CatalogEntries
import com.septianbeneran.urithiru.feature.catalog.catalog.CatalogEntry
import com.septianbeneran.urithiru.feature.catalog.screen.demo.ComponentDemo
import com.septianbeneran.urithiru.feature.catalog.viewmodel.CatalogComponentViewModel

@FeatureRoute(
    routeParams = CatalogComponentRoute::class
)
@Composable
fun CatalogComponentRoute(
    navigator: Navigator
) {
    val viewModel: CatalogComponentViewModel = hiltViewModel()
    val entry = viewModel.entry ?: return

    BaseScreen(
        viewModel = viewModel,
        topAppBarArgs = defaults(title = entry.name)
    ) {
        CatalogComponentScreen(entry = entry)
    }
}

@Composable
fun CatalogComponentScreen(entry: CatalogEntry) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = entry.description,
            style = UrithiruTheme.typography.bodyM,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ComponentDemo(id = entry.id)
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogComponentScreenPrev() {
    UrithiruTheme {
        CatalogComponentScreen(entry = requireNotNull(CatalogEntries.find("button")))
    }
}
