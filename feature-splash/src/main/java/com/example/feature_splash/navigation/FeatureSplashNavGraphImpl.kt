package com.example.feature_splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.core_navigation.FeatureNavGraph
import com.example.core_navigation.Navigator
import com.example.core_navigation.graph.FeatureSplashNavGraph
import com.example.core_navigation.route.feature_b.LandingRoute
import com.example.core_navigation.route.feature_splash.SplashRoute
import com.example.feature_splash.screen.SplashScreen
import javax.inject.Inject

class FeatureSplashNavGraphImpl @Inject constructor() : FeatureNavGraph() {
    override fun createGraph(navGraphBuilder: NavGraphBuilder, navigator: Navigator) {
        navGraphBuilder.navigation<FeatureSplashNavGraph>(
            startDestination = SplashRoute
        ) {
            composable<SplashRoute> {
                SplashScreen(
                    onNavigateToLanding = {
                        navigator.navigate(LandingRoute)
                    }
                )
            }
        }
    }
}