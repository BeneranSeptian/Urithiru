package com.example.feature.a.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.core.navigation.util.FeatureNavGraph
import com.example.core.navigation.util.Navigator
import com.example.core.navigation.graph.FeatureANavGraph
import com.example.core.navigation.route.feature_a.WeaponDetailRoute
import com.example.core.navigation.route.feature_a.WeaponListRoute
import com.example.feature.a.screen.WeaponDetailRoute
import com.example.feature.a.screen.WeaponListRouteScreen
import javax.inject.Inject

class FeatureANavGraphImpl @Inject constructor() : FeatureNavGraph() {
    override fun createGraph(navGraphBuilder: NavGraphBuilder, navigator: Navigator) {
        navGraphBuilder.navigation<FeatureANavGraph>(
            startDestination = WeaponListRoute
        ) {
            composable<WeaponListRoute>() {
                WeaponListRouteScreen(navigator)
            }

            composable<WeaponDetailRoute> {
                WeaponDetailRoute(navigator)
            }
        }
    }
}