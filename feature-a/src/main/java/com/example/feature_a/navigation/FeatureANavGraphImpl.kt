package com.example.feature_a.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.core_navigation.FeatureNavGraph
import com.example.core_navigation.Navigator
import com.example.core_navigation.graph.FeatureANavGraph
import com.example.core_navigation.route.feature_a.WeaponDetailRoute
import com.example.core_navigation.route.feature_a.WeaponListRoute
import com.example.feature_a.screen.WeaponDetailScreen
import com.example.feature_a.screen.WeaponListScreen
import com.example.feature_a.viewmodel.WeaponDetailViewModel
import com.example.feature_a.viewmodel.WeaponListViewModel
import javax.inject.Inject

class FeatureANavGraphImpl @Inject constructor() : FeatureNavGraph() {
    override fun createGraph(navGraphBuilder: NavGraphBuilder, navigator: Navigator) {
        navGraphBuilder.navigation<FeatureANavGraph>(
            startDestination = WeaponListRoute
        ) {
            composable<WeaponListRoute> {
                val viewModel: WeaponListViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                WeaponListScreen(
                    state = state,
                    viewModel = viewModel,
                    onAction = viewModel::onAction,
                    onNavigateToWeaponDetail = { weaponId ->
                        navigator.navigate(WeaponDetailRoute(weaponId))
                    }
                )
            }

            composable<WeaponDetailRoute> {
                val viewModel: WeaponDetailViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                WeaponDetailScreen(
                    viewModel = viewModel,
                    state = state,
                    onAction = viewModel::onAction
                )
            }
        }
    }
}