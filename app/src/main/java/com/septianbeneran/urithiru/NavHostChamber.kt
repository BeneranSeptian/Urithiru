package com.septianbeneran.urithiru

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.septianbeneran.urithiru.core.ui.base.LocalBottomOverlayHeight
import com.septianbeneran.urithiru.core.ui.component.UriNavigationBar
import com.septianbeneran.urithiru.core.ui.component.UriNavigationBarDefaults
import com.septianbeneran.urithiru.core.ui.component.UriNavigationBarItem
import com.septianbeneran.urithiru.core.ui.component.UriNavigationBarStyle
import com.septianbeneran.urithiru.core.ui.component.UriNavigationLabelMode
import com.septianbeneran.urithiru.graph.registerAllFeatureGraphs

/**
 * The app's single NavHost: a floating bottom bar on phones, a rail (inside a NavigationSuiteScaffoldLayout)
 * on larger screens. The bar only shows on the root screen of a [TopLevelDestination] graph, so detail
 * screens, splash, onboarding and login are full screen. Both sit in our own AnimatedVisibility, not the
 * scaffold's built-in show/hide, because the scaffold's animation can't be customized.
 *
 * The phone bar floats over the screens instead of taking space below them, so content scrolls behind it.
 * Its height goes to the screens as [LocalBottomOverlayHeight]; BaseScreen keeps content clear of it, or
 * lets a scrolling screen run behind it with matching bottom padding.
 *
 * The bar's visibility follows every screen that is visible, not just the current one, so it only comes
 * back once a back animation has finished.
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

    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfoV2())
    val isBottomBar = layoutType == NavigationSuiteType.NavigationBar
    val barItems = TopLevelDestination.entries.map {
        UriNavigationBarItem(stringResource(it.label), it.icon, it.selectedIcon)
    }

    val bottomOverlayHeight = if (isBottomBar && isBarVisible) {
        NavigationBarStyle.height + NavigationBarStyle.bottomMargin
    } else {
        0.dp
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavigationSuiteScaffoldLayout(
            navigationSuiteType = if (isBottomBar) NavigationSuiteType.None else layoutType,
            navigationSuite = {
                if (!isBottomBar) {
                    AnimatedVisibility(
                        visible = isBarVisible,
                        enter = railEnterTransition(),
                        exit = railExitTransition()
                    ) {
                        NavigationSuite(layoutType = layoutType) {
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
                                    label = { Text(stringResource(tab.label)) }
                                )
                            }
                        }
                    }
                }
            }
        ) {
            Box(
                modifier = if (isBarVisible && !isBottomBar) {
                    Modifier.consumeWindowInsets(WindowInsets.navigationBars)
                } else {
                    Modifier
                }
            ) {
                CompositionLocalProvider(LocalBottomOverlayHeight provides bottomOverlayHeight) {
                    AppNavHost(navController = navController, navigator = navigator)
                }
            }
        }

        if (isBottomBar) {
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = isBarVisible,
                enter = barEnterTransition(),
                exit = barExitTransition()
            ) {
                UriNavigationBar(
                    items = barItems,
                    selectedIndex = TopLevelDestination.entries.indexOf(currentTab),
                    onSelect = { navigator.navigateToTopLevel(TopLevelDestination.entries[it].graph) },
                    style = NavigationBarStyle,
                    colors = UriNavigationBarDefaults.colors()
                )
            }
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

private val NavigationBarStyle = UriNavigationBarStyle(
    isFloating = true,
    shape = RoundedCornerShape(28.dp),
    horizontalMargin = 16.dp,
    bottomMargin = 12.dp,
    shadowElevation = 8.dp,
    height = 64.dp,
    showIndicator = true,
    indicatorShape = RoundedCornerShape(22.dp),
    indicatorPadding = 6.dp,
    labelMode = UriNavigationLabelMode.SelectedOnly,
    selectedIconScale = 1.1f,
    springDampingRatio = Spring.DampingRatioLowBouncy,
    springStiffness = Spring.StiffnessMediumLow,
    hapticFeedback = true
)

private const val SLIDE_DURATION_MS = 150
private const val TAB_FADE_DURATION_MS = 100

private fun <T> slideSpec() = tween<T>(SLIDE_DURATION_MS, easing = FastOutSlowInEasing)

private const val BAR_ENTER_DURATION_MS = 100
private const val BAR_EXIT_DURATION_MS = 100

/** Change these to restyle how the bar (phones) and the rail (larger screens) appear and disappear. */
private fun barEnterTransition(): EnterTransition =
    slideInVertically(tween(BAR_ENTER_DURATION_MS, easing = FastOutSlowInEasing)) { it } +
        fadeIn(tween(BAR_ENTER_DURATION_MS))

private fun barExitTransition(): ExitTransition =
    slideOutVertically(tween(BAR_EXIT_DURATION_MS, easing = FastOutSlowInEasing)) { it } +
        fadeOut(tween(BAR_EXIT_DURATION_MS))

private fun railEnterTransition(): EnterTransition =
    expandHorizontally(tween(BAR_ENTER_DURATION_MS, easing = FastOutSlowInEasing)) + fadeIn(tween(BAR_ENTER_DURATION_MS))

private fun railExitTransition(): ExitTransition =
    shrinkHorizontally(tween(BAR_EXIT_DURATION_MS, easing = FastOutSlowInEasing)) + fadeOut(tween(BAR_EXIT_DURATION_MS))

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
