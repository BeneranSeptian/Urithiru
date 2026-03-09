package com.septianbeneran.urithiru.feature.b.screen

import android.Manifest.permission.READ_CONTACTS
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureBNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_a.WeaponListRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_b.LandingRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_b.OnBoardingRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.util.permission.rememberActionPermissionHandler
import com.septianbeneran.urithiru.feature.b.viewmodel.HomeScreenViewModel

@FeatureRoute(
    routeParams = LandingRoute::class,
    graph = FeatureBNavGraph::class
)
@Composable
fun HomeScreen(
    navigator: Navigator,
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<HomeScreenViewModel>()
    BaseScreen(
        viewModel = viewModel
    ) { properties ->
        val permissionHandler = rememberActionPermissionHandler(properties)
        var isPermissionGranted by remember { mutableStateOf(false) }
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                navigator.navigate(OnBoardingRoute)
            }) {
                Text(text = "On Boarding")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navigator.navigate(WeaponListRoute) }
            ) {
                Text(text = "Weapon List")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    permissionHandler.startAction(
                        permissions = setOf(READ_CONTACTS),
                        onPermissionGranted = { isPermissionGranted = true },
                        onPermissionDenied = { isPermissionGranted = false }
                    )
                }
            ) {
                if (isPermissionGranted) {
                    Text(text = "Permission Granted")
                } else {
                    Text(text = "Request Permission")
                }
            }
        }
    }
}
