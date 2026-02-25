package com.example.feature.a.screen.stateaction

import com.septianbeneran.template.core.base.BaseState
import com.septianbeneran.template.core.entity.a.Weapon

data class WeaponDetailScreenUiState(
    val weaponDetailState: BaseState<Weapon> = BaseState.StateInitial
)

sealed interface WeaponDetailAction {
    data class GetWeaponDetail(val weaponId: String) : WeaponDetailAction
}

sealed interface WeaponDetailNonce
