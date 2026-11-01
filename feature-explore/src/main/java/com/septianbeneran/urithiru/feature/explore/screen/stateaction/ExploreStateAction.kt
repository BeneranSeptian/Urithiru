package com.septianbeneran.urithiru.feature.explore.screen.stateaction

import com.septianbeneran.urithiru.core.base.BaseState
import com.septianbeneran.urithiru.core.base.BaseState.StateInitial
import com.septianbeneran.urithiru.core.entity.eldenring.Boss
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumSearchResult
import com.septianbeneran.urithiru.core.entity.eldenring.Location
import com.septianbeneran.urithiru.core.remote.entity.AppError
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
    val kindPages: Map<CompendiumKind, Int> = emptyMap(),
    val endedKinds: Set<CompendiumKind> = emptySet(),
    val failedKinds: Set<CompendiumKind> = emptySet(),
    val isEndReached: Boolean = false,
    val isRefreshing: Boolean = false,
    val refreshError: AppError? = null
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

    val regionRows: List<RegionRow>
        get() = if (isSearching || selectedKind != null) {
            emptyList()
        } else {
            (featured + entries)
                .mapNotNull { entry -> entry.region?.let { region -> region to entry } }
                .groupBy({ it.first }, { it.second })
                .filterValues { it.size >= MIN_REGION_ROW_SIZE }
                .entries
                .sortedByDescending { it.value.size }
                .take(MAX_REGION_ROWS)
                .map { (region, regionEntries) -> RegionRow(region, regionEntries) }
        }

    fun withPagingReset(): ExploreUiState = copy(
        featured = emptyList(),
        entries = emptyList(),
        searchResult = null,
        page = 0,
        kindPages = emptyMap(),
        endedKinds = emptySet(),
        failedKinds = emptySet(),
        isEndReached = false
    )

    private companion object {
        const val MIN_REGION_ROW_SIZE = 4
        const val MAX_REGION_ROWS = 2
    }
}

data class RegionRow(
    val region: String,
    val entries: List<CompendiumEntry>
)

private val CompendiumEntry.region: String?
    get() = when (this) {
        is Boss -> region
        is Location -> region
        else -> null
    }

sealed interface ExploreAction {
    data object Refresh : ExploreAction
    data object LoadNextPage : ExploreAction
    data object DismissRefreshError : ExploreAction
    data class OnSearchTextChange(val text: String) : ExploreAction
    data class OnSearch(val query: String) : ExploreAction
    data object OnClearSearch : ExploreAction
    data class OnKindSelect(val kind: CompendiumKind?) : ExploreAction
    data class OnEntryClick(val entry: CompendiumEntry) : ExploreAction
}

sealed interface ExploreNonce : BaseNonce {
    data class NavigateToDetail(val kind: CompendiumKind, val id: String) : ExploreNonce
}
