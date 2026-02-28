package com.example.feature.splash.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import com.example.core.navigation.annotation.FeatureRoute
import com.example.core.navigation.graph.TestingGraph
import com.example.core.navigation.routeparams.splash.TestingRouteParams
import com.example.core.navigation.util.Navigator
//import com.example.feature.splash.route.testing2Route

@FeatureRoute(
    routeParams = TestingRouteParams::class,
    graph = TestingGraph::class
)
@Composable
fun TestingRoute(navigator: Navigator) {
    Box(contentAlignment = Center){
        Text("this is TESTING SCREEN")
    }
}

//public fun NavGraphBuilder.splashGraph(
//    navigator: Navigator
//) {
//    navigation<FeatureSplashNavGraph>(startDestination = TestingRouteParams) {
//        exampleRouteGenerated(navigator)
//        testingRouteGenerated(navigator)
//    }
//}