package com.example.feature_a.viewmodel

import com.example.feature_a.screen.stateaction.WeaponListScreenAction
import com.example.feature_a.screen.stateaction.WeaponListScreenAction.GetWeaponList
import com.example.feature_a.screen.stateaction.WeaponListScreenUiState
import com.septianbeneran.template.api_a.domain.get.GetWeaponListUseCase
import com.septianbeneran.template.core.base.BaseState.StateFailed
import com.septianbeneran.template.core.base.BaseState.StateLoading
import com.septianbeneran.template.core.base.BaseState.StateSuccess
import com.septianbeneran.template.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeaponListViewModel @Inject constructor(
    private val getWeaponListUseCase: GetWeaponListUseCase
) : BaseViewModel<WeaponListScreenUiState>(WeaponListScreenUiState()) {

    fun onAction(action: WeaponListScreenAction) {
        when (action) {
            is GetWeaponList -> getWeaponList()
        }
    }

    private fun getWeaponList() {
        collectApi(
            flow = getWeaponListUseCase(),
            onLoading = { updateUiState { it.copy(weaponListState = StateLoading) } },
            onError = { error ->
                updateUiState { it.copy(weaponListState = StateFailed(error)) }
            },
            onSuccess = { data ->
                data?.let { weaponList ->
                    updateUiState { it.copy(weaponListState = StateSuccess(weaponList)) }
                }
            }
        )
    }
}