package com.septianbeneran.urithiru.feature.profile.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureProfileNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_profile.ProfileRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriEmptyState
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.feature.profile.viewmodel.ProfileViewModel

@FeatureRoute(
    routeParams = ProfileRoute::class,
    graph = FeatureProfileNavGraph::class
)
@Composable
fun ProfileRoute(
    navigator: Navigator
) {
    val viewModel: ProfileViewModel = hiltViewModel()

    BaseScreen(
        viewModel = viewModel,
        topAppBarArgs = defaults(title = "Profile", backIcon = null)
    ) {
        ProfileScreen()
    }
}

@Composable
fun ProfileScreen() {
    UriEmptyState(
        title = "Profile",
        message = "Your account, the theme setting and logout are coming here next.",
        icon = R.drawable.person
    )
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPrev() {
    UrithiruTheme {
        ProfileScreen()
    }
}
