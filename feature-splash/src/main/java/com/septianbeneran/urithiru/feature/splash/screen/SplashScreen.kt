package com.septianbeneran.urithiru.feature.splash.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureSplashNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_b.LandingRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_splash.SplashRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.R
import kotlinx.coroutines.delay

@FeatureRoute(
    routeParams = SplashRoute::class,
    graph = FeatureSplashNavGraph::class
)
@Composable
fun SplashScreen(
    navigator: Navigator
) {
    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LaunchedEffect(Unit) {
            delay(2000)
            navigator.navigate(LandingRoute)
        }
        Image(
            modifier = Modifier.wrapContentSize().align(Center).padding(horizontal = 24.dp),
            painter = painterResource(R.drawable.splash_logo),
            contentDescription = "Splash Logo"
        )
    }
}