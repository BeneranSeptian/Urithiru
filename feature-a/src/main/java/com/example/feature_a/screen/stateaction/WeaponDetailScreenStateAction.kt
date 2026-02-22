package com.example.feature_a.screen.stateaction

import com.septianbeneran.template.core.base.BaseState
import com.septianbeneran.template.core_entity.a.Weapon

data class WeaponDetailScreenUiState(
    val weaponDetailState: BaseState<Weapon> = BaseState.StateInitial
)

sealed interface WeaponDetailAction {
    data class GetWeaponDetail(val weaponId: String) : WeaponDetailAction
}
