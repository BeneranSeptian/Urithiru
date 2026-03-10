package com.septianbeneran.urithiru.feature.b.viewmodel

import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.b.screen.stateaction.PersonalizeExperienceScreenAction
import com.septianbeneran.urithiru.feature.b.screen.stateaction.PersonalizeExperienceScreenAction.OnClickInterest
import com.septianbeneran.urithiru.feature.b.screen.stateaction.PersonalizeExperienceScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PersonalizeExperienceViewModel @Inject constructor() : BaseViewModel() {

    private val _uiState = MutableStateFlow(PersonalizeExperienceScreenState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: PersonalizeExperienceScreenAction) {
        when(action){
            is OnClickInterest -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        interestList = currentState.interestList.mapIndexed { index, item ->
                            if (index == action.index) {
                                item.copy(isSelected = !item.isSelected)
                            } else {
                                item
                            }
                        }
                    )
                }
            }
        }
    }
}