package com.example.feature_a.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.core_navigation.FeatureNavGraph
import com.example.core_navigation.Navigator
import com.example.core_navigation.graph.FeatureANavGraph
import com.example.core_navigation.route.feature_a.WeaponDetailRoute
import com.example.core_navigation.route.feature_a.WeaponListRoute
import com.example.feature_a.screen.WeaponDetailRoute
import com.example.feature_a.screen.WeaponListRouteScreen
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