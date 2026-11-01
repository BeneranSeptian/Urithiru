package com.septianbeneran.urithiru

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.septianbeneran.urithiru.core.navigation.graph.FeatureCatalogNavGraph
import com.septianbeneran.urithiru.core.navigation.graph.FeatureExploreNavGraph
import com.septianbeneran.urithiru.core.navigation.graph.FeatureProfileNavGraph
import com.septianbeneran.urithiru.core.navigation.graph.FeatureShopNavGraph
import com.septianbeneran.urithiru.core.ui.R

/**
 * The tabs of the navigation bar (or rail), in display order. Each points at a feature's nav graph;
 * the first one is home. Adding a tab is one entry here plus a feature module with that graph.
 */
enum class TopLevelDestination(
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val selectedIcon: Int,
    val graph: Any
) {
    Catalog(R.string.common_title_catalog, R.drawable.widgets, R.drawable.widgets_filled, FeatureCatalogNavGraph),
    Shop(R.string.common_title_shop, R.drawable.shopping_bag, R.drawable.shopping_bag_filled, FeatureShopNavGraph),
    Explore(R.string.common_title_explore, R.drawable.explore, R.drawable.explore_filled, FeatureExploreNavGraph),
    Profile(R.string.common_title_profile, R.drawable.person, R.drawable.person_filled, FeatureProfileNavGraph)
}
