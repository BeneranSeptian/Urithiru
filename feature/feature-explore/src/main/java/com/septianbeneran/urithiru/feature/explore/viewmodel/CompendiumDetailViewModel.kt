package com.septianbeneran.urithiru.feature.explore.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumDetailUseCase
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateLoading
import com.septianbeneran.urithiru.core.base.BaseState.StateSuccess
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_explore.CompendiumDetailRoute
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.CompendiumDetailAction
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.CompendiumDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CompendiumDetailViewModel @Inject constructor(
    private val getCompendiumDetailUseCase: GetCompendiumDetailUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CompendiumDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val route = savedStateHandle.toRoute<CompendiumDetailRoute>()
    private val kind = CompendiumKind.entries.firstOrNull { it.name == route.kind }

    init {
        load()
    }

    fun onAction(action: CompendiumDetailAction) {
        when (action) {
            is CompendiumDetailAction.Retry -> load()
        }
    }

    private fun load() {
        if (kind == null) {
            _uiState.update { it.copy(detailState = StateFailed(AppError.NotFound)) }
            return
        }

        collectApi(
            flow = getCompendiumDetailUseCase(kind, route.id),
            key = DETAIL_KEY,
            onLoading = { _uiState.update { it.copy(detailState = StateLoading) } },
            onError = { error -> _uiState.update { it.copy(detailState = StateFailed(error)) } },
            onSuccess = { entry ->
                _uiState.update {
                    it.copy(detailState = if (entry != null) StateSuccess(entry) else StateFailed(AppError.NotFound))
                }
            }
        )
    }

    private companion object {
        const val DETAIL_KEY = "compendiumDetail"
    }
}
