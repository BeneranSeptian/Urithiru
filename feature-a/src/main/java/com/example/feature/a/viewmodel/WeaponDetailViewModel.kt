package com.example.feature.a.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.core.navigation.routeparams.feature_a.WeaponDetailRoute
import com.example.core.ui.base.BaseViewModel
import com.example.feature.a.screen.stateaction.WeaponDetailAction
import com.example.feature.a.screen.stateaction.WeaponDetailAction.GetWeaponDetail
import com.example.feature.a.screen.stateaction.WeaponDetailScreenUiState
import com.septianbeneran.template.api.a.domain.get.GetWeaponDetailUseCase
import com.septianbeneran.template.core.base.BaseState.StateFailed
import com.septianbeneran.template.core.base.BaseState.StateSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WeaponDetailViewModel @Inject constructor(
    private val getWeaponDetailUseCase: GetWeaponDetailUseCase,
    savedStateHandle: SavedStateHandle
): BaseViewModel() {

    private val _uiState = MutableStateFlow(WeaponDetailScreenUiState())
    val uiState = _uiState.asStateFlow()

    val weaponId: String by lazy {
        savedStateHandle.toRoute<WeaponDetailRoute>().id
    }

    init {
        onAction(GetWeaponDetail(weaponId))
    }

    fun onAction(action: WeaponDetailAction) {
        when(action) {
            is GetWeaponDetail -> getWeaponDetail(action.weaponId)
        }
    }

    private fun getWeaponDetail(weaponId: String) {
        collectApi(
            flow = getWeaponDetailUseCase(weaponId),
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    weaponDetailState = StateSuccess(it)
                )
            },
            onError = { error ->
                _uiState.value = _uiState.value.copy(
                    weaponDetailState = StateFailed(error)
                )
            }
        )
    }
}