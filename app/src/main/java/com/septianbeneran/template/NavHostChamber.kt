package com.septianbeneran.template

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.core_navigation.FeatureNavGraph
import com.example.core_navigation.Navigator
import com.example.core_navigation.graph.FeatureSplashNavGraph

@Composable
fun NavHostChamber(
    navgraphs: Set<FeatureNavGraph>
) {
    val navController = rememberNavController()
    val navigator = remember { Navigator(navController, navgraphs) }

    NavHost(
        navController = navController,
        startDestination = FeatureSplashNavGraph,
        enterTransition = {
            slideIntoContainer(
                towards = Left,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = Left,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        },
        popEnterTransition = { slideIntoContainer(Right) },
        popExitTransition = { slideOutOfContainer(Right) }
    ) {
        navgraphs.forEach { navGraph ->
            navGraph.createGraph(this, navigator)
        }
    }
}