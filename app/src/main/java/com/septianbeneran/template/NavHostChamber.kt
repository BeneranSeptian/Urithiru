package com.septianbeneran.template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.core_navigation.FeatureNavGraph
import com.example.core_navigation.Navigator
import com.example.core_navigation.graph.FeatureANavGraph
import com.example.core_navigation.graph.FeatureBNavGraph

@Composable
fun NavHostChamber(
    navgraphs: Set<FeatureNavGraph>
) {
    val navController = rememberNavController()
    val navigator = remember {
        Navigator(navController, navgraphs)
    }


    NavHost(navController, startDestination = FeatureBNavGraph) {
        navgraphs.forEach { navGraph ->
            navGraph.createGraph(this, navigator)
        }
    }
}