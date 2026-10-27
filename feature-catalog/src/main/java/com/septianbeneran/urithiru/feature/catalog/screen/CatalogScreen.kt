package com.septianbeneran.urithiru.feature.catalog.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureCatalogNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_catalog.CatalogComponentRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_catalog.CatalogRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriDivider
import com.septianbeneran.urithiru.core.ui.component.UriListItem
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.feature.catalog.catalog.CatalogEntries
import com.septianbeneran.urithiru.feature.catalog.catalog.CatalogSection
import com.septianbeneran.urithiru.feature.catalog.viewmodel.CatalogViewModel

@FeatureRoute(
    routeParams = CatalogRoute::class,
    graph = FeatureCatalogNavGraph::class
)
@Composable
fun CatalogRoute(
    navigator: Navigator
) {
    val viewModel: CatalogViewModel = hiltViewModel()

    BaseScreen(
        viewModel = viewModel,
        topAppBarArgs = defaults(title = "Catalog", backIcon = null),
        contentPadding = PaddingValues(start = 8.dp, top = 24.dp, end = 8.dp)
    ) {
        CatalogScreen(
            sections = CatalogEntries.sections,
            onEntryClick = { id -> navigator.navigate(CatalogComponentRoute(id)) }
        )
    }
}

@Composable
fun CatalogScreen(
    sections: List<CatalogSection>,
    onEntryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        sections.forEach { section ->
            item(key = section.title) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 4.dp),
                    text = section.title,
                    style = UrithiruTheme.typography.h5,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(section.entries, key = { it.id }) { entry ->
                UriListItem(
                    modifier = Modifier.clickable { onEntryClick(entry.id) },
                    title = entry.name,
                    description = entry.description,
                    trailingContent = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(R.drawable.chevron_right),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                UriDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogScreenPrev() {
    UrithiruTheme {
        CatalogScreen(sections = CatalogEntries.sections, onEntryClick = {})
    }
}
