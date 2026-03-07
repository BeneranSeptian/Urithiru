package com.septianbeneran.urithiru.feature.a.screen.stateaction

import com.septianbeneran.urithiru.core.base.BaseState
import com.septianbeneran.urithiru.core.base.BaseState.StateInitial
import com.septianbeneran.urithiru.core.entity.a.Weapon
import com.septianbeneran.urithiru.core.ui.base.BaseNonce

data class WeaponListScreenUiState(
    val searchWeaponText: String = "",
    val weapons: List<Weapon> = emptyList(),
    val weaponListState: BaseState<List<Weapon>> = StateInitial,
    val isEndReached: Boolean = false,
    val weaponListLocal: List<Weapon> = emptyList(),
    val page: Int = 0
)

sealed interface WeaponListScreenAction {
    data object GetWeaponList: WeaponListScreenAction
    data object LoadNextPage: WeaponListScreenAction
    data class OnSearchWeaponTextChange(val newValue: String): WeaponListScreenAction
    data class OnSearchButtonClick(val weaponName: String): WeaponListScreenAction
    data object GetWeaponListLocal: WeaponListScreenAction
}

sealed interface WeaponListNonce: BaseNonce {
    data class NavigateToWeaponDetail(val weaponId: String): WeaponListNonce
}