package com.septianbeneran.urithiru.feature.onboarding.screen.stateaction

import com.septianbeneran.urithiru.core.entity.onboarding.OnBoardingPageData
import com.septianbeneran.urithiru.core.ui.base.BaseNonce

data class OnBoardingScreenState(
    val onBoardingPageDataList: List<OnBoardingPageData> = emptyList()
)

sealed interface OnBoardingScreenAction {
    object OnClickNextLastPage: OnBoardingScreenAction
}

sealed interface OnBoardingScreenNonce: BaseNonce {
    object NavigateToPersonalizeExperienceScreen: OnBoardingScreenNonce
}