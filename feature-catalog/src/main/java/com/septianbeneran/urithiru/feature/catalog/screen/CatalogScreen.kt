package com.septianbeneran.urithiru.feature.catalog.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureCatalogNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_catalog.CatalogRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriEmptyState
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
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
        topAppBarArgs = defaults(title = "Catalog", backIcon = null)
    ) {
        CatalogScreen()
    }
}

@Composable
fun CatalogScreen() {
    UriEmptyState(
        title = "Catalog",
        message = "Every Uri* component in every state is coming here next.",
        icon = R.drawable.widgets
    )
}

@Preview(showBackground = true)
@Composable
private fun CatalogScreenPrev() {
    UrithiruTheme {
        CatalogScreen()
    }
}
