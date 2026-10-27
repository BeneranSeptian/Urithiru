package com.septianbeneran.urithiru

import androidx.annotation.DrawableRes
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
    val label: String,
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val selectedIcon: Int,
    val graph: Any
) {
    Catalog("Catalog", R.drawable.widgets, R.drawable.widgets_filled, FeatureCatalogNavGraph),
    Shop("Shop", R.drawable.shopping_bag, R.drawable.shopping_bag_filled, FeatureShopNavGraph),
    Explore("Explore", R.drawable.explore, R.drawable.explore_filled, FeatureExploreNavGraph),
    Profile("Profile", R.drawable.person, R.drawable.person_filled, FeatureProfileNavGraph)
}
