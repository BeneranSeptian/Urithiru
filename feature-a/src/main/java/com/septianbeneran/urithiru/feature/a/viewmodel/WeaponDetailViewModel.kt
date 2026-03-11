package com.septianbeneran.urithiru.feature.a.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.septianbeneran.urithiru.api.a.domain.get.GetWeaponDetailUseCase
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateSuccess
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_a.WeaponDetailRoute
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.a.screen.stateaction.WeaponDetailAction
import com.septianbeneran.urithiru.feature.a.screen.stateaction.WeaponDetailAction.GetWeaponDetail
import com.septianbeneran.urithiru.feature.a.screen.stateaction.WeaponDetailScreenUiState
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
                    weapon = it
                )
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