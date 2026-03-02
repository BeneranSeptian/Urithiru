package com.example.core.navigation.util

import androidx.navigation.NavHostController
import androidx.navigation.toRoute

class Navigator(
    val navController: NavHostController
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

    fun navigateUp() = navController.navigateUp()

    inline fun <reified T> getCurrentRoute() = navController.currentBackStackEntry?.toRoute<T>()
}