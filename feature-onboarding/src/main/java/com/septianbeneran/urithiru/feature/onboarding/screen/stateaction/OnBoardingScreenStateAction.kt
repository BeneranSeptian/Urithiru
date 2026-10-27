package com.septianbeneran.urithiru.feature.onboarding.screen.stateaction

import com.septianbeneran.urithiru.core.entity.onboarding.OnBoardingPageData
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.base.BaseNonce

data class OnBoardingScreenState(
    val pages: List<OnBoardingPageData> = OnBoardingDefaults.pages,
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

object OnBoardingDefaults {
    val pages = listOf(
        OnBoardingPageData(
            title = "Built from real components",
            description = "Every screen in this app uses the same Uri* components. What you see here is live, not a screenshot."
        ),
        OnBoardingPageData(
            title = "Errors, handled for you",
            description = "Go offline and watch the loading, error and retry states that every screen gets for free."
        ),
        OnBoardingPageData(
            title = "Make it yours",
            description = "Pick a theme. It's saved on your device, and the whole app switches right away."
        ),
        OnBoardingPageData(
            title = "Sign in or explore",
            description = "Try the login flow with a DummyJSON demo account, or go straight into the app."
        )
    )
}
