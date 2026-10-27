package com.septianbeneran.urithiru.feature.splash.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureSplashNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_onboarding.OnBoardingRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_splash.SplashRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.util.NonceObserver
import com.septianbeneran.urithiru.feature.splash.screen.stateaction.SplashScreenNonce
import com.septianbeneran.urithiru.feature.splash.viewmodel.SplashScreenViewModel

@FeatureRoute(
    routeParams = SplashRoute::class,
    graph = FeatureSplashNavGraph::class
)
@Composable
fun SplashScreen(
    navigator: Navigator
) {
    val viewModel: SplashScreenViewModel = hiltViewModel()

    NonceObserver(viewModel.nonce) { nonce ->
        when (nonce) {
            SplashScreenNonce.NavigateToOnBoarding -> navigator.navigate(
                route = OnBoardingRoute,
                popUpTo = SplashRoute,
                inclusive = true
            )
            SplashScreenNonce.NavigateToHome -> navigator.navigateToHome()
        }
    }

    BaseScreen(viewModel = viewModel) {
        Box(
            contentAlignment = Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 24.dp),
                painter = painterResource(R.drawable.splash_logo),
                contentDescription = "Splash Logo"
            )
        }
    }
}
