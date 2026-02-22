package com.example.feature_b.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.core_navigation.FeatureNavGraph
import com.example.core_navigation.Navigator
import com.example.core_navigation.graph.FeatureBNavGraph
import com.example.core_navigation.route.feature_a.WeaponListRoute
import com.example.core_navigation.route.feature_b.LandingRoute
import com.example.feature_b.screen.HomeScreen
import javax.inject.Inject

class FeatureBNavGraphImpl @Inject constructor() : FeatureNavGraph() {
    override fun createGraph(navGraphBuilder: NavGraphBuilder, navigator: Navigator) {
        navGraphBuilder.navigation<FeatureBNavGraph>(
            startDestination = LandingRoute
        ) {
            composable<LandingRoute> {
                HomeScreen(
                    onNavigateToBossList = {

                    },
                    onNavigateToWeaponList = {
                        navigator.navigate(WeaponListRoute)
                    }
                )
            }
        }
    }
}