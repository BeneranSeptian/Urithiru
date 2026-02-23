package com.example.feature_a.viewmodel

import com.example.feature_a.screen.stateaction.WeaponListScreenAction
import com.example.feature_a.screen.stateaction.WeaponListScreenAction.GetWeaponList
import com.example.feature_a.screen.stateaction.WeaponListScreenAction.LoadNextPage
import com.example.feature_a.screen.stateaction.WeaponListScreenAction.OnSearchButtonClick
import com.example.feature_a.screen.stateaction.WeaponListScreenAction.OnSearchWeaponTextChange
import com.example.feature_a.screen.stateaction.WeaponListScreenUiState
import com.septianbeneran.template.api_a.domain.get.GetWeaponListUseCase
import com.septianbeneran.template.core.base.BaseState.StateFailed
import com.septianbeneran.template.core.base.BaseState.StateLoading
import com.septianbeneran.template.core.base.BaseState.StateSuccess
import com.example.core_ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WeaponListViewModel @Inject constructor(
    private val getWeaponListUseCase: GetWeaponListUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(WeaponListScreenUiState())
    val uiState = _uiState.asStateFlow()
    fun onAction(action: WeaponListScreenAction) {
        when (action) {
            is GetWeaponList -> {
                _uiState.update { it.copy(page = 0, weapons = emptyList(), isEndReached = false) }
                getWeaponList()
            }

            is LoadNextPage -> {
                if (!_uiState.value.isEndReached && _uiState.value.weaponListState !is StateLoading) {
                    getWeaponList()
                }
            }

            is OnSearchButtonClick -> {
                _uiState.update { it.copy(page = 0, weapons = emptyList(), isEndReached = false) }
                getWeaponList(action.weaponName)
            }

            is OnSearchWeaponTextChange -> _uiState.update {
                it.copy(
                    searchWeaponText = action.newValue
                )
            }
        }
    }

    private fun getWeaponList(name: String? = null) {
        val currentState = _uiState.value
        val isFirstPage = currentState.page == 0

        collectApi(
            flow = getWeaponListUseCase(
                name = name ?: currentState.searchWeaponText.takeIf { it.isNotEmpty() },
                page = currentState.page,
            ),
            isCentralLoading = isFirstPage,
            onLoading = {
                _uiState.update { it.copy(weaponListState = StateLoading) }
            },
            onError = { error ->
                _uiState.update { it.copy(weaponListState = StateFailed(error)) }
            },
            onSuccess = { data ->
                val newWeapons = data ?: emptyList()
                _uiState.update {
                    it.copy(
                        weapons = it.weapons + newWeapons,
                        weaponListState = StateSuccess(it.weapons + newWeapons),
                        page = it.page + 1,
                        isEndReached = newWeapons.size < 20
                    )
                }
            }
        )
    }
}