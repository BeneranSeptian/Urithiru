package com.septianbeneran.urithiru.feature.explore.viewmodel

import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumFeedPageUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumListUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.SearchCompendiumUseCase
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateInitial
import com.septianbeneran.urithiru.core.base.BaseState.StateLoading
import com.septianbeneran.urithiru.core.base.BaseState.StateSuccess
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction
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

    init {
        restart()
    }

    fun onAction(action: ExploreAction) {
        when (action) {
            is Refresh -> {
                seed = Random.nextLong()
                restart()
            }

            is LoadNextPage -> {
                val state = _uiState.value
                if (!state.isGroupedSearch && !state.isEndReached && state.feedState !is StateLoading) loadPage()
            }

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
        _uiState.update {
            it.copy(
                featured = emptyList(),
                entries = emptyList(),
                searchResult = null,
                feedState = StateInitial,
                page = 0,
                endedKinds = emptySet(),
                isEndReached = false
            )
        }
        if (_uiState.value.isGroupedSearch) searchAllSections() else loadPage()
    }

    private fun searchAllSections() {
        collectApi(
            flow = searchCompendiumUseCase(_uiState.value.submittedQuery),
            key = FEED_KEY,
            onLoading = ::onPageLoading,
            onError = ::onPageError,
            onSuccess = { result ->
                _uiState.update {
                    it.copy(
                        searchResult = result,
                        feedState = StateSuccess(result?.groups.orEmpty().flatMap { group -> group.entries }),
                        isEndReached = true
                    )
                }
            }
        )
    }

    private fun loadPage() {
        val state = _uiState.value
        val name = state.submittedQuery.takeIf { it.isNotEmpty() }
        when (val kind = state.selectedKind) {
            null -> loadMixedPage(state, name)
            else -> loadSectionPage(state, kind, name)
        }
    }

    private fun loadMixedPage(state: ExploreUiState, name: String?) {
        collectApi(
            flow = getCompendiumFeedPageUseCase(
                page = state.page,
                kinds = CompendiumKind.entries.toSet() - state.endedKinds,
                seed = seed,
                name = name
            ),
            key = FEED_KEY,
            onLoading = ::onPageLoading,
            onError = ::onPageError,
            onSuccess = { feedPage ->
                appendPage(
                    newEntries = feedPage?.entries.orEmpty(),
                    pickFeaturedPerKind = true,
                    endedKinds = feedPage?.endedKinds.orEmpty(),
                    isLastPage = { ended -> ended.containsAll(CompendiumKind.entries) }
                )
            }
        )
    }

    private fun loadSectionPage(state: ExploreUiState, kind: CompendiumKind, name: String?) {
        collectApi(
            flow = getCompendiumListUseCase(kind = kind, limit = PAGE_SIZE, name = name, page = state.page),
            key = FEED_KEY,
            onLoading = ::onPageLoading,
            onError = ::onPageError,
            onSuccess = { page ->
                val newEntries = page?.entries.orEmpty()
                val loadedCount = state.entries.size + state.featured.size + newEntries.size
                appendPage(
                    newEntries = newEntries,
                    pickFeaturedPerKind = false,
                    endedKinds = emptySet(),
                    isLastPage = {
                        newEntries.size < PAGE_SIZE || page?.total?.let { total -> loadedCount >= total } == true
                    }
                )
            }
        )
    }

    private fun appendPage(
        newEntries: List<CompendiumEntry>,
        pickFeaturedPerKind: Boolean,
        endedKinds: Set<CompendiumKind>,
        isLastPage: (Set<CompendiumKind>) -> Boolean
    ) {
        _uiState.update {
            val featured = if (it.page == 0 && !it.isSearching) pickFeatured(newEntries, pickFeaturedPerKind) else it.featured
            val entries = (it.entries + (newEntries - featured.toSet())).distinctBy { entry -> entry.kind to entry.id }
            val ended = it.endedKinds + endedKinds
            it.copy(
                featured = featured,
                entries = entries,
                feedState = StateSuccess(entries),
                page = it.page + 1,
                endedKinds = ended,
                isEndReached = isLastPage(ended)
            )
        }
    }

    private fun onPageLoading() {
        _uiState.update { it.copy(feedState = StateLoading) }
    }

    private fun onPageError(error: AppError) {
        _uiState.update { it.copy(feedState = StateFailed(error)) }
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
