package com.septianbeneran.urithiru.feature.explore.screen.stateaction

import com.septianbeneran.urithiru.core.base.BaseState
import com.septianbeneran.urithiru.core.base.BaseState.StateInitial
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumSearchResult
import com.septianbeneran.urithiru.core.ui.base.BaseNonce

data class ExploreUiState(
    val searchText: String = "",
    val submittedQuery: String = "",
    val selectedKind: CompendiumKind? = null,
    val featured: List<CompendiumEntry> = emptyList(),
    val entries: List<CompendiumEntry> = emptyList(),
    val searchResult: CompendiumSearchResult? = null,
    val feedState: BaseState<List<CompendiumEntry>> = StateInitial,
    val page: Int = 0,
    val endedKinds: Set<CompendiumKind> = emptySet(),
    val isEndReached: Boolean = false
) {
    val isSearching: Boolean
        get() = submittedQuery.isNotBlank()

    val isGroupedSearch: Boolean
        get() = isSearching && selectedKind == null

    val hasContent: Boolean
        get() = featured.isNotEmpty() ||
            entries.isNotEmpty() ||
            searchResult?.topResult != null ||
            !searchResult?.groups.isNullOrEmpty()
}

sealed interface ExploreAction {
    data object Refresh : ExploreAction
    data object LoadNextPage : ExploreAction
    data class OnSearchTextChange(val text: String) : ExploreAction
    data class OnSearch(val query: String) : ExploreAction
    data object OnClearSearch : ExploreAction
    data class OnKindSelect(val kind: CompendiumKind?) : ExploreAction
    data class OnEntryClick(val entry: CompendiumEntry) : ExploreAction
}

sealed interface ExploreNonce : BaseNonce {
    data class NavigateToDetail(val kind: CompendiumKind, val id: String) : ExploreNonce
}
