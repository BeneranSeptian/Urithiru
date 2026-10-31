package com.septianbeneran.urithiru.feature.profile.screen.stateaction

import com.septianbeneran.urithiru.core.base.BaseState
import com.septianbeneran.urithiru.core.entity.auth.User
import com.septianbeneran.urithiru.core.entity.auth.UserProfile
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.base.BaseNonce

data class ProfileUiState(
    val isSessionLoaded: Boolean = false,
    val user: User? = null,
    val profileState: BaseState<UserProfile> = BaseState.StateInitial,
    val themeMode: ThemeMode = ThemeMode.System,
    val showLogoutDialog: Boolean = false
)

sealed interface ProfileAction {
    data object OnSignInClick : ProfileAction
    data object OnRetryProfileClick : ProfileAction
    data class OnThemeModeSelect(val themeMode: ThemeMode) : ProfileAction
    data object OnReplayOnboardingClick : ProfileAction
    data object OnLogoutClick : ProfileAction
    data object OnLogoutConfirm : ProfileAction
    data object OnLogoutDismiss : ProfileAction
}

sealed interface ProfileNonce : BaseNonce {
    data object NavigateToLogin : ProfileNonce
    data object NavigateToOnboarding : ProfileNonce
}
