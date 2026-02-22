package com.example.core_navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.Navigator

open class FeatureNavGraph {
    open fun createGraph(
        navGraphBuilder: NavGraphBuilder,
        navigator: com.example.core_navigation.Navigator
    ) = Unit
}