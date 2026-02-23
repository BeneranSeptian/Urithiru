package com.example.feature_a.screen.stateaction

import com.example.core_ui.base.BaseNonce
import com.septianbeneran.template.core.base.BaseState
import com.septianbeneran.template.core.base.BaseState.StateInitial
import com.septianbeneran.template.core_entity.a.Weapon

data class WeaponListScreenUiState(
    val searchWeaponText: String = "",
    val weapons: List<Weapon> = emptyList(),
    val weaponListState: BaseState<List<Weapon>> = StateInitial,
    val isEndReached: Boolean = false,
    val page: Int = 0
)

sealed interface WeaponListScreenAction {
    data object GetWeaponList: WeaponListScreenAction
    data object LoadNextPage: WeaponListScreenAction
    data class OnSearchWeaponTextChange(val newValue: String): WeaponListScreenAction
    data class OnSearchButtonClick(val weaponName: String): WeaponListScreenAction
}

sealed interface WeaponListNonce: BaseNonce {
    data class NavigateToWeaponDetail(val weaponId: String): WeaponListNonce
}