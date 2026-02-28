package com.example.feature.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.navigation.routeparams.splash.TestingRouteParams
import com.example.core.navigation.util.Navigator

internal fun NavGraphBuilder.testingRoute(navigator: Navigator) {
    composable<TestingRouteParams> {
        //HomeRoute(navigator)
    }
}