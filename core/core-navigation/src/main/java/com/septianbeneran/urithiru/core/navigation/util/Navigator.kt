package com.septianbeneran.urithiru.core.navigation.util

import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.toRoute

class Navigator(
    val navController: NavHostController,
    private val homeGraph: Any
) {
    fun navigate(
        route: Any,
        popUpTo: Any? = null,
        inclusive: Boolean = false,
        launchSingleTop: Boolean = false
    ) {
        navController.navigate(route) {
            popUpTo?.let {
                this.popUpTo(it) {
                    this.inclusive = inclusive
                }
            }

            this.launchSingleTop = launchSingleTop
        }
    }

    /** Opens the first tab and clears everything before it, e.g. after splash, onboarding or login. */
    fun navigateToHome() {
        navController.navigate(homeGraph) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    /**
     * Switches to a top-level tab. Each tab keeps its own back stack: leaving saves it, and coming back
     * restores it. Back from any tab returns to the first tab.
     */
    fun navigateToTopLevel(graph: Any) {
        val homeStartDestination = (navController.graph.findNode(homeGraph) as? NavGraph)?.findStartDestination()
        navController.navigate(graph) {
            homeStartDestination?.let {
                popUpTo(it.id) { saveState = true }
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateUp() = navController.navigateUp()

    inline fun <reified T> getCurrentRoute() = navController.currentBackStackEntry?.toRoute<T>()
}
