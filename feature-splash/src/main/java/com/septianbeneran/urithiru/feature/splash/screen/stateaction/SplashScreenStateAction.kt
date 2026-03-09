package com.septianbeneran.urithiru.feature.splash.screen.stateaction

import com.septianbeneran.urithiru.core.base.BaseState
import com.septianbeneran.urithiru.core.base.BaseState.StateInitial
import com.septianbeneran.urithiru.core.entity.a.Weapon
import com.septianbeneran.urithiru.core.entity.b.OnBoardingPageData
import com.septianbeneran.urithiru.core.ui.base.BaseNonce

data class SplashScreenUiState(
    val onBoardingPageDataList: List<OnBoardingPageData> = emptyList(),
)

sealed interface SplashScreenAction {
    data object GetOnBoardingPageDataList: SplashScreenAction
}

sealed interface SplashScreenNonce: BaseNonce {
    data object NavigateToOnBoarding: SplashScreenNonce
}