package com.septianbeneran.urithiru.feature.splash.screen.stateaction

import com.septianbeneran.urithiru.core.ui.base.BaseNonce

sealed interface SplashScreenNonce : BaseNonce {
    data object NavigateToOnBoarding : SplashScreenNonce
    data object NavigateToHome : SplashScreenNonce
}
