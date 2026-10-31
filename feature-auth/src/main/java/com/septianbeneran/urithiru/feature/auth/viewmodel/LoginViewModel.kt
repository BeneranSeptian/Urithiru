package com.septianbeneran.urithiru.feature.auth.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.septianbeneran.urithiru.api.dummyjson.domain.post.PostLoginUseCase
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_auth.LoginRoute
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginAction
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginAction.OnPasswordChange
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginAction.OnSignInClick
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginAction.OnUseDemoAccountClick
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginAction.OnUsernameChange
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginNonce
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val postLoginUseCase: PostLoginUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val returnToPrevious = savedStateHandle.toRoute<LoginRoute>().returnToPrevious

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is OnUsernameChange -> _uiState.update { it.copy(username = action.username, error = null) }
            is OnPasswordChange -> _uiState.update { it.copy(password = action.password, error = null) }
            OnUseDemoAccountClick -> _uiState.update {
                it.copy(username = DEMO_USERNAME, password = DEMO_PASSWORD, error = null)
            }
            OnSignInClick -> signIn()
        }
    }

    private fun signIn() {
        val state = _uiState.value
        if (!state.canSubmit) return

        collectApi(
            flow = postLoginUseCase(state.username.trim(), state.password),
            key = LOGIN_KEY,
            onLoading = { _uiState.update { it.copy(isLoading = true, error = null) } },
            onError = { error -> _uiState.update { it.copy(isLoading = false, error = error) } },
            onSuccess = {
                _uiState.update { it.copy(isLoading = false) }
                sendNonce(if (returnToPrevious) LoginNonce.NavigateBack else LoginNonce.NavigateToHome)
            }
        )
    }

    private companion object {
        const val LOGIN_KEY = "login"
        const val DEMO_USERNAME = "emilys"
        const val DEMO_PASSWORD = "emilyspass"
    }
}
