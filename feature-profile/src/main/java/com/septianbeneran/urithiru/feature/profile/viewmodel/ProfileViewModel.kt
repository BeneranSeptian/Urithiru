package com.septianbeneran.urithiru.feature.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.septianbeneran.urithiru.api.dummyjson.domain.clear.ClearSessionUseCase
import com.septianbeneran.urithiru.api.dummyjson.domain.get.GetProfileUseCase
import com.septianbeneran.urithiru.api.dummyjson.domain.load.LoadSessionUseCase
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateInitial
import com.septianbeneran.urithiru.core.base.BaseState.StateLoading
import com.septianbeneran.urithiru.core.base.BaseState.StateSuccess
import com.septianbeneran.urithiru.core.entity.auth.User
import com.septianbeneran.urithiru.core.settings.AppSettings
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileAction
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileAction.OnLogoutClick
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileAction.OnLogoutConfirm
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileAction.OnLogoutDismiss
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileAction.OnReplayOnboardingClick
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileAction.OnRetryProfileClick
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileAction.OnSignInClick
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileAction.OnThemeModeSelect
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileNonce
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val loadSessionUseCase: LoadSessionUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val appSettings: AppSettings
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadSessionUseCase().map { it?.user }.distinctUntilChanged().collect(::onUserChange)
        }
        collectLocalData(appSettings.themeMode.map { it ?: ThemeMode.System }) { themeMode ->
            _uiState.update { it.copy(themeMode = themeMode) }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            OnSignInClick -> sendNonce(ProfileNonce.NavigateToLogin)
            OnRetryProfileClick -> loadProfile()
            is OnThemeModeSelect -> viewModelScope.launch { appSettings.saveThemeMode(action.themeMode) }
            OnReplayOnboardingClick -> sendNonce(ProfileNonce.NavigateToOnboarding)
            OnLogoutClick -> _uiState.update { it.copy(showLogoutDialog = true) }
            OnLogoutDismiss -> _uiState.update { it.copy(showLogoutDialog = false) }
            OnLogoutConfirm -> {
                _uiState.update { it.copy(showLogoutDialog = false) }
                viewModelScope.launch { clearSessionUseCase() }
            }
        }
    }

    private fun onUserChange(user: User?) {
        _uiState.update {
            it.copy(
                isSessionLoaded = true,
                user = user,
                profileState = if (user == null) StateInitial else it.profileState
            )
        }
        if (user != null) loadProfile()
    }

    private fun loadProfile() {
        collectApi(
            flow = getProfileUseCase(),
            key = PROFILE_KEY,
            onLoading = { _uiState.update { it.copy(profileState = StateLoading) } },
            onError = { error -> updateProfileState { it.copy(profileState = StateFailed(error)) } },
            onSuccess = { profile -> updateProfileState { it.copy(profileState = StateSuccess(profile)) } }
        )
    }

    private fun updateProfileState(transform: (ProfileUiState) -> ProfileUiState) {
        _uiState.update { if (it.user == null) it else transform(it) }
    }

    private companion object {
        const val PROFILE_KEY = "profile"
    }
}
