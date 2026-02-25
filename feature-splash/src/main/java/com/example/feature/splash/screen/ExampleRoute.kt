package com.example.feature.splash.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import com.example.core.navigation.annotation.FeatureRoute
import com.example.core.navigation.routeparams.ExampleRouteParams
import com.example.core.navigation.util.Navigator

@FeatureRoute(
    route = ExampleRouteParams::class
)
@Composable
fun ExampleRoute(navigator: Navigator) {
    Box(contentAlignment = Center){
        Text("this is EXAMPLE SCREEN")
    }
}