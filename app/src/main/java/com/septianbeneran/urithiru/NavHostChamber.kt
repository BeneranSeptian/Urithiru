package com.septianbeneran.urithiru

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.septianbeneran.urithiru.core.navigation.graph.FeatureSplashNavGraph
import com.septianbeneran.urithiru.graph.registerAllFeatureGraphs

@Composable
fun NavHostChamber() {
    val navController = rememberNavController()
    val navigator = remember {
        _root_ide_package_.com.septianbeneran.urithiru.core.navigation.util.Navigator(
            navController
        )
    }

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
        registerAllFeatureGraphs(navigator)
    }
}