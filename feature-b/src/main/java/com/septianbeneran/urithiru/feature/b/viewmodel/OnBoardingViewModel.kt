package com.septianbeneran.urithiru.feature.b.viewmodel

import com.septianbeneran.urithiru.api.b.domain.load.LoadOnBoardingPageDataListUseCase
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.b.screen.stateaction.OnBoardingScreenAction
import com.septianbeneran.urithiru.feature.b.screen.stateaction.OnBoardingScreenAction.OnClickNextLastPage
import com.septianbeneran.urithiru.feature.b.screen.stateaction.OnBoardingScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    loadOnBoardingPageDataListUseCase: LoadOnBoardingPageDataListUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingScreenState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: OnBoardingScreenAction) {
        when(action) {
            is OnClickNextLastPage -> TODO()
        }
    }

    init {
        collectLocalData(
            loadOnBoardingPageDataListUseCase(),
        ) {list ->
            _uiState.update { it.copy(onBoardingPageDataList = list) }
        }
    }
}