package com.septianbeneran.urithiru.feature.onboarding.viewmodel

import androidx.lifecycle.viewModelScope
import com.septianbeneran.urithiru.api.dummyjson.domain.load.LoadSessionUseCase
import com.septianbeneran.urithiru.api.jsonbin.domain.load.LoadOnBoardingPageDataListUseCase
import com.septianbeneran.urithiru.core.settings.AppSettings
import com.septianbeneran.urithiru.core.settings.ThemeMode
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.DemoRequestState
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingDefaults
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenAction
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenAction.OnContinueClick
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenAction.OnDemoOfflineChange
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenAction.OnDemoRetryClick
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenAction.OnSignInClick
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenAction.OnThemeModeSelect
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenNonce
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.OnBoardingScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    loadOnBoardingPageDataListUseCase: LoadOnBoardingPageDataListUseCase,
    loadSessionUseCase: LoadSessionUseCase,
    private val appSettings: AppSettings
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingScreenState())
    val uiState = _uiState.asStateFlow()

    private var demoRequestJob: Job? = null

    init {
        collectLocalData(loadOnBoardingPageDataListUseCase()) { remotePages ->
            _uiState.update { state ->
                state.copy(
                    pages = OnBoardingDefaults.pages.mapIndexed { index, fallback ->
                        remotePages.getOrNull(index) ?: fallback
                    }
                )
            }
        }
        collectLocalData(appSettings.themeMode.map { it ?: ThemeMode.System }) { themeMode ->
            _uiState.update { it.copy(themeMode = themeMode) }
        }
        collectLocalData(loadSessionUseCase().map { it?.user?.firstName.orEmpty() }) { name ->
            _uiState.update { it.copy(signedInName = name.ifBlank { null }) }
        }
    }

    fun onAction(action: OnBoardingScreenAction) {
        when (action) {
            is OnDemoOfflineChange -> {
                _uiState.update { it.copy(isDemoOffline = action.isOffline) }
                runDemoRequest()
            }
            OnDemoRetryClick -> runDemoRequest()
            is OnThemeModeSelect -> viewModelScope.launch { appSettings.saveThemeMode(action.themeMode) }
            OnSignInClick -> finishOnboarding(OnBoardingScreenNonce.NavigateToLogin)
            OnContinueClick -> finishOnboarding(OnBoardingScreenNonce.NavigateToHome)
        }
    }

    private fun runDemoRequest() {
        demoRequestJob?.cancel()
        demoRequestJob = viewModelScope.launch {
            _uiState.update { it.copy(demoRequestState = DemoRequestState.Loading) }
            delay(DEMO_REQUEST_DURATION_MS)
            _uiState.update {
                it.copy(demoRequestState = if (it.isDemoOffline) DemoRequestState.Error else DemoRequestState.Success)
            }
        }
    }

    private fun finishOnboarding(nonce: OnBoardingScreenNonce) {
        viewModelScope.launch {
            appSettings.setOnboardingCompleted()
            sendNonce(nonce)
        }
    }

    private companion object {
        const val DEMO_REQUEST_DURATION_MS = 1200L
    }
}
