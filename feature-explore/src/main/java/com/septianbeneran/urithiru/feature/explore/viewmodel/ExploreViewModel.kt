package com.septianbeneran.urithiru.feature.explore.viewmodel

import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumFeedPageUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumListUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.SearchCompendiumUseCase
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateInitial
import com.septianbeneran.urithiru.core.base.BaseState.StateLoading
import com.septianbeneran.urithiru.core.base.BaseState.StateSuccess
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumFeedPage
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumSearchResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction.DismissRefreshError
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction.LoadNextPage
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction.OnClearSearch
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction.OnEntryClick
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction.OnKindSelect
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction.OnSearch
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction.OnSearchTextChange
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction.Refresh
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreNonce.NavigateToDetail
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getCompendiumFeedPageUseCase: GetCompendiumFeedPageUseCase,
    private val getCompendiumListUseCase: GetCompendiumListUseCase,
    private val searchCompendiumUseCase: SearchCompendiumUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private var seed = Random.nextLong()
    private var stateBeforeRefresh: ExploreUiState? = null

    init {
        restart()
    }

    fun onAction(action: ExploreAction) {
        when (action) {
            is Refresh -> {
                seed = Random.nextLong()
                if (_uiState.value.hasContent) refresh() else restart()
            }

            is LoadNextPage -> {
                val state = _uiState.value
                val canLoadMore = !state.isGroupedSearch && !state.isEndReached && !state.isRefreshing
                if (canLoadMore && state.feedState !is StateLoading) load(state)
            }

            is DismissRefreshError -> _uiState.update { it.copy(refreshError = null) }

            is OnSearchTextChange -> _uiState.update { it.copy(searchText = action.text) }

            is OnSearch -> {
                _uiState.update { it.copy(submittedQuery = action.query.trim()) }
                restart()
            }

            is OnClearSearch -> {
                _uiState.update { it.copy(searchText = "", submittedQuery = "") }
                restart()
            }

            is OnKindSelect -> if (action.kind != _uiState.value.selectedKind) {
                _uiState.update { it.copy(selectedKind = action.kind) }
                restart()
            }

            is OnEntryClick -> sendNonce(NavigateToDetail(action.entry.kind, action.entry.id))
        }
    }

    private fun restart() {
        stateBeforeRefresh = null
        _uiState.update { it.withPagingReset().copy(feedState = StateInitial, isRefreshing = false, refreshError = null) }
        load(_uiState.value)
    }

    private fun refresh() {
        stateBeforeRefresh = _uiState.value
        _uiState.update { it.copy(isRefreshing = true, refreshError = null) }
        load(_uiState.value.withPagingReset())
    }

    private fun load(from: ExploreUiState) {
        val name = from.submittedQuery.takeIf { it.isNotEmpty() }
        when {
            from.isGroupedSearch -> searchAllSections(from.submittedQuery)
            from.selectedKind == null -> loadMixedPage(from, name)
            else -> loadSectionPage(from, from.selectedKind, name)
        }
    }

    private fun searchAllSections(query: String) {
        collectApi(
            flow = searchCompendiumUseCase(query),
            key = FEED_KEY,
            onLoading = ::onPageLoading,
            onError = ::onPageError,
            onSuccess = { result -> updateFromBase { it.withSearchResult(result) } }
        )
    }

    private fun loadMixedPage(from: ExploreUiState, name: String?) {
        val kinds = CompendiumKind.entries - from.endedKinds
        collectApi(
            flow = getCompendiumFeedPageUseCase(
                pages = kinds.associateWith { from.kindPages[it] ?: 0 },
                seed = seed,
                name = name
            ),
            key = FEED_KEY,
            onLoading = ::onPageLoading,
            onError = ::onPageError,
            onSuccess = { feedPage -> updateFromBase { it.withMixedPage(feedPage, kinds) } }
        )
    }

    private fun loadSectionPage(from: ExploreUiState, kind: CompendiumKind, name: String?) {
        collectApi(
            flow = getCompendiumListUseCase(kind = kind, limit = PAGE_SIZE, name = name, page = from.page),
            key = FEED_KEY,
            onLoading = ::onPageLoading,
            onError = ::onPageError,
            onSuccess = { page -> updateFromBase { it.withSectionPage(page) } }
        )
    }

    private fun updateFromBase(transform: (ExploreUiState) -> ExploreUiState) {
        stateBeforeRefresh = null
        _uiState.update { current ->
            val base = if (current.isRefreshing) current.withPagingReset() else current
            transform(base).copy(isRefreshing = false)
        }
    }

    private fun ExploreUiState.withSearchResult(result: CompendiumSearchResult?) = copy(
        searchResult = result,
        feedState = StateSuccess(result?.groups.orEmpty().flatMap { group -> group.entries }),
        isEndReached = true
    )

    private fun ExploreUiState.withMixedPage(feedPage: CompendiumFeedPage?, requested: List<CompendiumKind>): ExploreUiState {
        val failed = feedPage?.failedKinds.orEmpty()
        val ended = endedKinds + feedPage?.endedKinds.orEmpty()
        return appendEntries(feedPage?.entries.orEmpty(), featuredPerKind = true).copy(
            kindPages = kindPages + (requested - failed).associateWith { (kindPages[it] ?: 0) + 1 },
            endedKinds = ended,
            failedKinds = failed,
            isEndReached = ended.containsAll(CompendiumKind.entries)
        )
    }

    private fun ExploreUiState.withSectionPage(page: CompendiumPage?): ExploreUiState {
        val newEntries = page?.entries.orEmpty()
        val loadedCount = entries.size + featured.size + newEntries.size
        return appendEntries(newEntries, featuredPerKind = false).copy(
            isEndReached = newEntries.size < PAGE_SIZE || page?.total?.let { total -> loadedCount >= total } == true
        )
    }

    private fun ExploreUiState.appendEntries(newEntries: List<CompendiumEntry>, featuredPerKind: Boolean): ExploreUiState {
        val newFeatured = if (page == 0 && !isSearching) pickFeatured(newEntries, featuredPerKind) else featured
        val allEntries = (entries + (newEntries - newFeatured.toSet())).distinctBy { entry -> entry.kind to entry.id }
        return copy(
            featured = newFeatured,
            entries = allEntries,
            feedState = StateSuccess(allEntries),
            page = page + 1
        )
    }

    private fun onPageLoading() {
        if (!_uiState.value.isRefreshing) _uiState.update { it.copy(feedState = StateLoading) }
    }

    private fun onPageError(error: AppError) {
        val previous = stateBeforeRefresh
        stateBeforeRefresh = null
        _uiState.update {
            if (it.isRefreshing && previous != null) {
                previous.copy(isRefreshing = false, refreshError = error)
            } else {
                it.copy(feedState = StateFailed(error), isRefreshing = false)
            }
        }
    }

    private fun pickFeatured(entries: List<CompendiumEntry>, onePerKind: Boolean): List<CompendiumEntry> =
        entries
            .filter { it.image != null && !it.description.isNullOrBlank() }
            .let { candidates -> if (onePerKind) candidates.distinctBy { it.kind } else candidates }
            .take(FEATURED_COUNT)

    private companion object {
        const val FEED_KEY = "exploreFeed"
        const val PAGE_SIZE = 20
        const val FEATURED_COUNT = 5
    }
}
