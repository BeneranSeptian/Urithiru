package com.example.feature_a.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.core_navigation.route.feature_a.WeaponDetailRoute
import com.example.feature_a.screen.stateaction.WeaponDetailAction
import com.example.feature_a.screen.stateaction.WeaponDetailAction.GetWeaponDetail
import com.example.feature_a.screen.stateaction.WeaponDetailScreenUiState
import com.septianbeneran.template.api_a.domain.get.GetWeaponDetailUseCase
import com.septianbeneran.template.core.base.BaseState
import com.septianbeneran.template.core.base.BaseState.StateFailed
import com.septianbeneran.template.core.base.BaseState.StateLoading
import com.septianbeneran.template.core.base.BaseState.StateSuccess
import com.septianbeneran.template.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeaponDetailViewModel @Inject constructor(
    private val getWeaponDetailUseCase: GetWeaponDetailUseCase,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel<WeaponDetailScreenUiState>(WeaponDetailScreenUiState()) {

    init {
        val route = savedStateHandle.toRoute<WeaponDetailRoute>()
        onAction(GetWeaponDetail(route.id))
    }

    fun onAction(action: WeaponDetailAction) {
        when(action) {
            is GetWeaponDetail -> getWeaponDetail(action.weaponId)
        }
    }

    private fun getWeaponDetail(weaponId: String) {
        collectApi(
            flow = getWeaponDetailUseCase(weaponId),
            onLoading = { updateUiState { it.copy(weaponDetailState = StateLoading) }},
            onError = { error ->
                updateUiState { it.copy(weaponDetailState = StateFailed(error)) }
            },
            onSuccess = { data ->
                data?.let { weaponList ->
                    updateUiState { it.copy(weaponDetailState = StateSuccess(weaponList)) }
                }
            }
        )
    }
}