package com.septianbeneran.urithiru.feature.auth.screen.stateaction

import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.base.BaseNonce

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: AppError? = null
) {
    val canSubmit: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading
}

sealed interface LoginAction {
    data class OnUsernameChange(val username: String) : LoginAction
    data class OnPasswordChange(val password: String) : LoginAction
    data object OnUseDemoAccountClick : LoginAction
    data object OnSignInClick : LoginAction
}

sealed interface LoginNonce : BaseNonce {
    data object NavigateToHome : LoginNonce
}
