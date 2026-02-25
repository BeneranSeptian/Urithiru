package com.example.feature.splash.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import com.example.core.navigation.annotation.FeatureRoute
import com.example.core.navigation.routeparams.TestingRouteParams
import com.example.core.navigation.util.Navigator

@FeatureRoute(
    route = TestingRouteParams::class
)
@Composable
fun TestingRoute(navigator: Navigator) {
    Box(contentAlignment = Center){
        Text("this is TESTING SCREEN")
    }
}