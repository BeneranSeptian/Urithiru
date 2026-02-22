package com.example.feature_a.screen.stateaction

import com.septianbeneran.template.core.base.BaseState
import com.septianbeneran.template.core.base.BaseState.StateInitial
import com.septianbeneran.template.core_entity.a.Weapon

data class WeaponListScreenUiState(
    val weaponListState: BaseState<List<Weapon>> = StateInitial
)

sealed interface WeaponListScreenAction {
    data object GetWeaponList: WeaponListScreenAction
}