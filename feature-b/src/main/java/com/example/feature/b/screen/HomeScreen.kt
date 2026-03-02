package com.example.feature.b.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.navigation.annotation.FeatureRoute
import com.example.core.navigation.graph.FeatureBNavGraph
import com.example.core.navigation.routeparams.feature_a.WeaponListRoute
import com.example.core.navigation.routeparams.feature_b.LandingRoute
import com.example.core.navigation.util.Navigator

@FeatureRoute(
    routeParams = LandingRoute::class,
    graph = FeatureBNavGraph::class
)
@Composable
fun HomeScreen(
    navigator: Navigator,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {

        }) {
            Text(text = "Boss List")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navigator.navigate(WeaponListRoute) }
        ) {
            Text(text = "Weapon List")
        }
    }
}
