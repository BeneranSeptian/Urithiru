package com.example.feature.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.core.navigation.util.FeatureNavGraph
import com.example.core.navigation.util.Navigator
import com.example.core.navigation.graph.FeatureSplashNavGraph
import com.example.core.navigation.route.feature_b.LandingRoute
import com.example.core.navigation.route.feature_splash.SplashRoute
import com.example.feature.splash.screen.SplashScreen
import javax.inject.Inject

class FeatureSplashNavGraphImpl @Inject constructor() : FeatureNavGraph() {
    override fun createGraph(navGraphBuilder: NavGraphBuilder, navigator: Navigator) {
        navGraphBuilder.navigation<FeatureSplashNavGraph>(
            startDestination = SplashRoute
        ) {
            composable<SplashRoute> {
                SplashScreen(
                    onNavigateToLanding = {
                        navigator.navigate(LandingRoute, popUpTo = SplashRoute, inclusive = true)
                    }
                )
            }
        }
    }
}