package com.septianbeneran.urithiru.feature.shop.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureShopNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_shop.ShopRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriEmptyState
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.feature.shop.R
import com.septianbeneran.urithiru.feature.shop.viewmodel.ShopViewModel

@FeatureRoute(
    routeParams = ShopRoute::class,
    graph = FeatureShopNavGraph::class
)
@Composable
fun ShopRoute(
    navigator: Navigator
) {
    val viewModel: ShopViewModel = hiltViewModel()

    BaseScreen(
        viewModel = viewModel,
        topAppBarArgs = defaults(title = stringResource(CoreUiR.string.common_title_shop), backIcon = null)
    ) {
        ShopScreen()
    }
}

@Composable
fun ShopScreen() {
    UriEmptyState(
        title = stringResource(CoreUiR.string.common_title_shop),
        message = stringResource(R.string.shop_coming_soon),
        icon = CoreUiR.drawable.shopping_bag
    )
}

@Preview(showBackground = true)
@Composable
private fun ShopScreenPrev() {
    UrithiruTheme {
        ShopScreen()
    }
}
