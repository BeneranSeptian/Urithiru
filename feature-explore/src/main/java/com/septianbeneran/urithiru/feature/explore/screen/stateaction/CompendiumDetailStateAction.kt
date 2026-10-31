package com.septianbeneran.urithiru.feature.explore.screen.stateaction

import com.septianbeneran.urithiru.core.base.BaseState
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry

data class CompendiumDetailUiState(
    val detailState: BaseState<CompendiumEntry> = BaseState.StateInitial
)

sealed interface CompendiumDetailAction {
    data object Retry : CompendiumDetailAction
}
