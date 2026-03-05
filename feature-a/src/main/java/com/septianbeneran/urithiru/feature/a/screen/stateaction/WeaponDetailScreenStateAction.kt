package com.septianbeneran.urithiru.feature.a.screen.stateaction

import com.septianbeneran.urithiru.core.base.BaseState
import com.septianbeneran.urithiru.core.entity.a.Weapon

data class WeaponDetailScreenUiState(
    val weaponDetailState: BaseState<Weapon> = BaseState.StateInitial
)

sealed interface WeaponDetailAction {
    data class GetWeaponDetail(val weaponId: String) : WeaponDetailAction
}

sealed interface WeaponDetailNonce
