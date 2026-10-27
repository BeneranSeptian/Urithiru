package com.septianbeneran.urithiru

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.septianbeneran.urithiru.core.navigation.graph.FeatureSplashNavGraph
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.graph.registerAllFeatureGraphs

/**
 * The app's single NavHost, inside a NavigationSuiteScaffold: a bottom bar on phones, a rail on larger
 * screens. The bar only shows on the root screen of a [TopLevelDestination] graph, so detail screens,
 * splash, onboarding and login are full screen.
 *
 * The bar's visibility follows every screen that is visible, not just the current one, so it only comes
 * back once a back animation has finished. Showing it mid-animation would resize the screens as they slide.
 */
@Composable
fun NavHostChamber() {
    val navController = rememberNavController()
    val navigator = remember {
        Navigator(
            navController = navController,
            homeGraph = TopLevelDestination.entries.first().graph
        )
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val visibleEntries by navController.visibleEntries.collectAsState()
    val currentTab = TopLevelDestination.entries.firstOrNull { backStackEntry?.destination.isInside(it) }
    val isBarVisible = visibleEntries.areAllTabRoots()

    val navigationSuiteState = rememberNavigationSuiteScaffoldState(NavigationSuiteScaffoldValue.Hidden)
    LaunchedEffect(isBarVisible) {
        if (isBarVisible) navigationSuiteState.show() else navigationSuiteState.hide()
    }

    NavigationSuiteScaffold(
        layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfoV2()),
        state = navigationSuiteState,
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { tab ->
                val isSelected = tab == currentTab
                item(
                    selected = isSelected,
                    onClick = { navigator.navigateToTopLevel(tab.graph) },
                    icon = {
                        Icon(
                            painter = painterResource(if (isSelected) tab.selectedIcon else tab.icon),
                            contentDescription = null
                        )
                    },
                    label = { Text(tab.label) }
                )
            }
        }
    ) {
        Box(modifier = if (isBarVisible) Modifier.consumeWindowInsets(WindowInsets.navigationBars) else Modifier) {
            AppNavHost(navController = navController, navigator = navigator)
        }
    }
}

/**
 * Kept separate, with parameters that never change, so recompositions of the scaffold around it (bar
 * visibility, selected tab) never recompose the NavHost mid-transition.
 */
@Composable
private fun AppNavHost(
    navController: NavHostController,
    navigator: Navigator
) {
    NavHost(
        navController = navController,
        startDestination = FeatureSplashNavGraph,
        enterTransition = {
            if (isTabSwitch()) fadeIn(tween(TAB_FADE_DURATION_MS))
            else slideIntoContainer(towards = Left, animationSpec = slideSpec())
        },
        exitTransition = {
            if (isTabSwitch()) fadeOut(tween(TAB_FADE_DURATION_MS))
            else slideOutOfContainer(towards = Left, animationSpec = slideSpec())
        },
        popEnterTransition = {
            if (isTabSwitch()) fadeIn(tween(TAB_FADE_DURATION_MS))
            else slideIntoContainer(towards = Right, animationSpec = slideSpec())
        },
        popExitTransition = {
            if (isTabSwitch()) fadeOut(tween(TAB_FADE_DURATION_MS))
            else slideOutOfContainer(towards = Right, animationSpec = slideSpec())
        }
    ) {
        registerAllFeatureGraphs(navigator)
    }
}

private const val SLIDE_DURATION_MS = 300
private const val TAB_FADE_DURATION_MS = 200

private fun <T> slideSpec() = tween<T>(SLIDE_DURATION_MS, easing = FastOutSlowInEasing)

/** Moving between two tab roots crossfades; tabs are siblings, so a forward or back slide doesn't fit. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.isTabSwitch(): Boolean =
    initialState.destination.isTabRoot() && targetState.destination.isTabRoot()

private fun List<NavBackStackEntry>.areAllTabRoots(): Boolean {
    val screens = filter { it.destination !is NavGraph }
    return screens.isNotEmpty() && screens.all { it.destination.isTabRoot() }
}

private fun NavDestination.isTabRoot(): Boolean =
    TopLevelDestination.entries.any { isInside(it) } && isGraphStart()

private fun NavDestination?.isInside(tab: TopLevelDestination): Boolean =
    this?.hierarchy?.any { it.hasRoute(tab.graph::class) } == true

private fun NavDestination?.isGraphStart(): Boolean =
    this != null && parent?.startDestinationId == id
