package com.septianbeneran.urithiru.feature.onboarding.screen.stateaction

import androidx.annotation.StringRes
import com.septianbeneran.urithiru.core.entity.onboarding.OnBoardingPageData
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.base.BaseNonce
import com.septianbeneran.urithiru.feature.onboarding.R

data class OnBoardingScreenState(
    val remotePages: List<OnBoardingPageData> = emptyList(),
    val isDemoOffline: Boolean = false,
    val demoRequestState: DemoRequestState = DemoRequestState.Success,
    val themeMode: ThemeMode = ThemeMode.System,
    val signedInName: String? = null
)

enum class DemoRequestState {
    Loading,
    Error,
    Success
}

sealed interface OnBoardingScreenAction {
    data class OnDemoOfflineChange(val isOffline: Boolean) : OnBoardingScreenAction
    data object OnDemoRetryClick : OnBoardingScreenAction
    data class OnThemeModeSelect(val themeMode: ThemeMode) : OnBoardingScreenAction
    data object OnSignInClick : OnBoardingScreenAction
    data object OnContinueClick : OnBoardingScreenAction
}

sealed interface OnBoardingScreenNonce : BaseNonce {
    data object NavigateToLogin : OnBoardingScreenNonce
    data object NavigateToHome : OnBoardingScreenNonce
}

data class OnBoardingPageText(
    @param:StringRes val title: Int,
    @param:StringRes val description: Int
)

object OnBoardingDefaults {
    val pages = listOf(
        OnBoardingPageText(R.string.onboarding_components_title, R.string.onboarding_components_description),
        OnBoardingPageText(R.string.onboarding_errors_title, R.string.onboarding_errors_description),
        OnBoardingPageText(R.string.onboarding_theme_title, R.string.onboarding_theme_description),
        OnBoardingPageText(R.string.onboarding_sign_in_title, R.string.onboarding_sign_in_description)
    )
}
