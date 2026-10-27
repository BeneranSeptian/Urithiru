package com.septianbeneran.urithiru.feature.splash.viewmodel

import androidx.lifecycle.viewModelScope
import com.septianbeneran.urithiru.api.jsonbin.domain.get.GetOnBoardingDataListUseCase
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateLoading
import com.septianbeneran.urithiru.core.base.BaseState.StateSuccess
import com.septianbeneran.urithiru.core.ui.base.BaseNonce
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.splash.screen.stateaction.SplashScreenAction
import com.septianbeneran.urithiru.feature.splash.screen.stateaction.SplashScreenAction.GetOnBoardingPageDataList
import com.septianbeneran.urithiru.feature.splash.screen.stateaction.SplashScreenNonce
import com.septianbeneran.urithiru.feature.splash.screen.stateaction.SplashScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val getOnBoardingDataListUseCase: GetOnBoardingDataListUseCase
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(SplashScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: SplashScreenAction) {
        when (action) {
            GetOnBoardingPageDataList -> getOnBoardingPageDataList()
        }
    }

    init {
        onAction(GetOnBoardingPageDataList)
    }

    private fun getOnBoardingPageDataList() {
        collectApi(
            flow = getOnBoardingDataListUseCase(
                binId = "69addb9cae596e708f6de2ed"
            ),
            isCentralLoading = true,
            onLoading = {
                _uiState.update { it.copy(onBoardingPageDataState = StateLoading) }
            },
            onError = { error ->
                _uiState.update { it.copy(onBoardingPageDataState = StateFailed(error)) }
            },
            onSuccess = { data ->
                _uiState.update { it.copy(onBoardingPageDataState = StateSuccess(data)) }
                sendNonce(SplashScreenNonce.NavigateToOnBoarding)
            }
        )
    }
}