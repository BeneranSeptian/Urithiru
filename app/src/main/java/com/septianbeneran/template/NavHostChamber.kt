package com.septianbeneran.template

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.core.navigation.graph.TestingGraph
import com.example.core.navigation.util.Navigator
import com.septianbeneran.template.graph.registerAllFeatureGraphs

@Composable
fun NavHostChamber() {
    val navController = rememberNavController()
    val navigator = remember { Navigator(navController) }

    NavHost(
        navController = navController,
        startDestination = TestingGraph,
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
        registerAllFeatureGraphs(navigator)
    }
}