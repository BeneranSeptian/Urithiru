package com.septianbeneran.template

import com.septianbeneran.template.core_entity.weapon.Weapon
import com.septianbeneran.template.api_a.domain.get.GetWeaponDetailUseCase
import com.septianbeneran.template.api_a.domain.get.GetWeaponListUseCase
import com.septianbeneran.template.core.base.BaseState
import com.septianbeneran.template.core.base.BaseState.StateFailed
import com.septianbeneran.template.core.base.BaseState.StateInitial
import com.septianbeneran.template.core.base.BaseState.StateLoading
import com.septianbeneran.template.core.base.BaseState.StateSuccess
import com.septianbeneran.template.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getWeaponListUseCase: GetWeaponListUseCase,
    private val getWeaponDetailUseCase: GetWeaponDetailUseCase
) : BaseViewModel() {

    private val _weaponListState: MutableStateFlow<BaseState<List<Weapon>>> =
        MutableStateFlow(StateInitial)
    val weaponListState = _weaponListState.asStateFlow()

    private val _weaponDetailState: MutableStateFlow<BaseState<Weapon>> =
        MutableStateFlow(StateInitial)
    val weaponDetailState = _weaponListState.asStateFlow()

    fun getWeaponList() {
        collectApi(
            flow = getWeaponListUseCase(),
            onLoading = { _weaponListState.value = StateLoading },
            onError = { _weaponListState.value = StateFailed(it) },
            onSuccess = { it?.let { _weaponListState.value = StateSuccess(it) } }
        )
    }

    fun getWeaponDetail(weaponId: String) {
        collectApi(
            flow = getWeaponDetailUseCase(weaponId),
            onLoading = { _weaponDetailState.value = StateLoading },
            onError = { _weaponDetailState.value = StateFailed(it) },
            onSuccess = { it?.let { _weaponDetailState.value = StateSuccess(it) } }
        )
    }
}