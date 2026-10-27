package com.septianbeneran.urithiru.feature.splash.viewmodel

import androidx.lifecycle.viewModelScope
import com.septianbeneran.urithiru.api.jsonbin.domain.get.GetOnBoardingDataListUseCase
import com.septianbeneran.urithiru.core.settings.AppSettings
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.splash.screen.stateaction.SplashScreenNonce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val getOnBoardingDataListUseCase: GetOnBoardingDataListUseCase,
    private val appSettings: AppSettings
) : BaseViewModel() {

    init {
        viewModelScope.launch {
            if (appSettings.hasCompletedOnboarding.first()) {
                sendNonce(SplashScreenNonce.NavigateToHome)
            } else {
                getOnBoardingPageDataList()
            }
        }
    }

    private fun getOnBoardingPageDataList() {
        collectApi(
            flow = getOnBoardingDataListUseCase(binId = ONBOARDING_BIN_ID),
            onSuccess = { sendNonce(SplashScreenNonce.NavigateToOnBoarding) },
            onError = { sendNonce(SplashScreenNonce.NavigateToOnBoarding) }
        )
    }

    private companion object {
        const val ONBOARDING_BIN_ID = "6ae0e204ac6210605a56e0bf"
    }
}
