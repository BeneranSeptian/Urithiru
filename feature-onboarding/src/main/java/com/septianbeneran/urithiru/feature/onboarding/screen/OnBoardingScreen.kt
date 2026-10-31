package com.septianbeneran.urithiru.feature.onboarding.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_auth.LoginRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureOnboardingNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_onboarding.OnBoardingRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriThemePicker
import com.septianbeneran.urithiru.core.ui.component.UriButton
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Tertiary
import com.septianbeneran.urithiru.core.ui.component.UriDot
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.util.NonceObserver
import com.septianbeneran.urithiru.feature.onboarding.screen.demo.ComponentsDemo
import com.septianbeneran.urithiru.feature.onboarding.screen.demo.ErrorHandlingDemo
import com.septianbeneran.urithiru.feature.onboarding.screen.demo.SignInDemo
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenAction
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenNonce
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenState
import com.septianbeneran.urithiru.feature.onboarding.viewmodel.OnBoardingViewModel
import kotlinx.coroutines.launch

@FeatureRoute(
    routeParams = OnBoardingRoute::class,
    graph = FeatureOnboardingNavGraph::class
)
@Composable
fun OnBoardingRoute(
    navigator: Navigator
) {
    val viewModel: OnBoardingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NonceObserver(viewModel.nonce) { nonce ->
        when (nonce) {
            OnBoardingScreenNonce.NavigateToLogin -> navigator.navigate(LoginRoute)
            OnBoardingScreenNonce.NavigateToHome -> navigator.navigateToHome()
        }
    }

    BaseScreen(viewModel = viewModel) {
        OnBoardingScreen(
            uiState = uiState,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun OnBoardingScreen(
    uiState: OnBoardingScreenState,
    onAction: (OnBoardingScreenAction) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { uiState.pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == uiState.pages.lastIndex

    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isLastPage) {
                UriButton(
                    type = Tertiary,
                    text = "Skip",
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(uiState.pages.lastIndex) } }
                )
            }
        }

        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = pagerState
        ) { page ->
            OnBoardingPage(
                title = uiState.pages[page].title,
                description = uiState.pages[page].description
            ) {
                when (page) {
                    0 -> ComponentsDemo()
                    1 -> ErrorHandlingDemo(
                        isOffline = uiState.isDemoOffline,
                        requestState = uiState.demoRequestState,
                        onOfflineChange = { onAction(OnBoardingScreenAction.OnDemoOfflineChange(it)) },
                        onRetry = { onAction(OnBoardingScreenAction.OnDemoRetryClick) }
                    )
                    2 -> UriThemePicker(
                        selected = uiState.themeMode,
                        onSelect = { onAction(OnBoardingScreenAction.OnThemeModeSelect(it)) }
                    )
                    else -> SignInDemo(signedInName = uiState.signedInName)
                }
            }
        }

        PageIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            pagerState = pagerState
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (isLastPage) {
            UriButton(
                modifier = Modifier.fillMaxWidth(),
                text = uiState.signedInName?.let { "Continue as $it" } ?: "Sign in with demo account",
                onClick = {
                    onAction(
                        if (uiState.signedInName != null) OnBoardingScreenAction.OnContinueClick
                        else OnBoardingScreenAction.OnSignInClick
                    )
                }
            )
            if (uiState.signedInName == null) {
                UriButton(
                    modifier = Modifier.fillMaxWidth(),
                    type = Tertiary,
                    text = "Continue as guest",
                    onClick = { onAction(OnBoardingScreenAction.OnContinueClick) }
                )
            }
        } else {
            UriButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Next",
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }
            )
        }
    }
}

@Composable
private fun OnBoardingPage(
    title: String,
    description: String,
    demo: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            demo()
        }
        Text(
            text = title,
            style = UrithiruTheme.typography.h1,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = description,
            style = UrithiruTheme.typography.bodyM,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pagerState.pageCount) { index ->
            UriDot(isFilled = index == pagerState.currentPage)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnBoardingScreenPrev() {
    UrithiruTheme {
        OnBoardingScreen(uiState = OnBoardingScreenState(), onAction = {})
    }
}
