package com.septianbeneran.urithiru.feature.explore.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateInitial
import com.septianbeneran.urithiru.core.base.BaseState.StateLoading
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureExploreNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_explore.CompendiumDetailRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_explore.ExploreRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.base.LocalScreenBottomPadding
import com.septianbeneran.urithiru.core.ui.component.UriDivider
import com.septianbeneran.urithiru.core.ui.component.UriDot
import com.septianbeneran.urithiru.core.ui.component.UriEmptyState
import com.septianbeneran.urithiru.core.ui.component.UriErrorState
import com.septianbeneran.urithiru.core.ui.component.UriListFooter
import com.septianbeneran.urithiru.core.ui.component.UriSearchBar
import com.septianbeneran.urithiru.core.ui.component.UriSkeleton
import com.septianbeneran.urithiru.core.ui.component.UriToast
import com.septianbeneran.urithiru.core.ui.component.UriToastProperties.UriToastType
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.localization.localizedString
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.util.NonceObserver
import com.septianbeneran.urithiru.core.ui.util.extension.bleed
import com.septianbeneran.urithiru.core.ui.util.extension.pageScale
import com.septianbeneran.urithiru.core.ui.util.extension.toMessage
import com.septianbeneran.urithiru.feature.explore.R
import com.septianbeneran.urithiru.feature.explore.screen.feed.CompendiumFeedCard
import com.septianbeneran.urithiru.feature.explore.screen.feed.FeaturedEntryCard
import com.septianbeneran.urithiru.feature.explore.screen.feed.countLabel
import com.septianbeneran.urithiru.feature.explore.screen.feed.pluralLabel
import com.septianbeneran.urithiru.feature.explore.screen.feed.tagline
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
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.RegionRow
import com.septianbeneran.urithiru.feature.explore.screen.util.FEATURED_AUTO_SCROLL_MS
import com.septianbeneran.urithiru.feature.explore.screen.util.FeedSpacing
import com.septianbeneran.urithiru.feature.explore.screen.util.LOAD_MORE_THRESHOLD
import com.septianbeneran.urithiru.feature.explore.screen.util.ScreenMargin
import com.septianbeneran.urithiru.feature.explore.screen.util.SearchCardHeight
import com.septianbeneran.urithiru.feature.explore.screen.util.SearchCardWidth
import com.septianbeneran.urithiru.feature.explore.screen.util.SkeletonCardRatios
import com.septianbeneran.urithiru.feature.explore.viewmodel.ExploreViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import com.septianbeneran.urithiru.core.ui.R as CoreUiR


@FeatureRoute(
    routeParams = ExploreRoute::class,
    graph = FeatureExploreNavGraph::class
)
@Composable
fun ExploreRoute(
    navigator: Navigator
) {
    val viewModel: ExploreViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BaseScreen(
        viewModel = viewModel,
        contentPadding = PaddingValues(top = ScreenMargin),
        drawBehindBottomOverlay = true,
        topAppBarArgs = defaults(
            title = localizedString(CoreUiR.string.common_title_explore),
            backIcon = null
        )
    ) {
        ExploreScreen(
            uiState = uiState,
            onAction = viewModel::onAction
        )
    }

    NonceObserver(
        nonce = viewModel.nonce,
        onNonce = { nonce ->
            when (nonce) {
                is NavigateToDetail -> navigator.navigate(
                    CompendiumDetailRoute(
                        kind = nonce.kind.name,
                        id = nonce.id
                    )
                )
            }
        }
    )
}

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    onAction: (ExploreAction) -> Unit
) {
    Column {
        UriSearchBar(
            modifier = Modifier.padding(horizontal = ScreenMargin),
            value = uiState.searchText,
            onValueChange = { onAction(OnSearchTextChange(it)) },
            onSearch = { onAction(OnSearch(uiState.searchText)) },
            placeholder = localizedString(R.string.explore_search_placeholder),
            onClear = { onAction(OnClearSearch) }
        )

        val feedState = uiState.feedState

        when {
            feedState is StateFailed && !uiState.hasContent -> {
                Column {
                    KindChips(
                        modifier = Modifier.padding(top = 16.dp),
                        selected = uiState.selectedKind,
                        onSelect = { onAction(OnKindSelect(it)) }
                    )
                    UriErrorState(
                        error = feedState.failed,
                        onRetry = { onAction(Refresh) }
                    )
                }
            }

            (feedState is StateLoading || feedState is StateInitial) && !uiState.hasContent -> {
                FeedSkeleton(
                    selectedKind = uiState.selectedKind,
                    showFeatured = !uiState.isSearching,
                    onSelectKind = { onAction(OnKindSelect(it)) }
                )
            }

            !uiState.hasContent -> {
                Column {
                    KindChips(
                        modifier = Modifier.padding(top = 16.dp),
                        selected = uiState.selectedKind,
                        onSelect = { onAction(OnKindSelect(it)) }
                    )
                    UriEmptyState(
                        title = localizedString(R.string.explore_nothing_found),
                        message = localizedString(
                            if (uiState.isSearching) CoreUiR.string.common_try_different_name
                            else R.string.explore_try_another_section
                        )
                    )
                }
            }

            else -> RefreshableContent(
                state = uiState,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun RefreshableContent(
    state: ExploreUiState,
    onAction: (ExploreAction) -> Unit
) {
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = state.isRefreshing,
        onRefresh = { onAction(Refresh) }
    ) {
        if (state.isGroupedSearch) {
            SearchResults(state = state, onAction = onAction)
        } else {
            ExploreFeed(state = state, onAction = onAction)
        }

        state.refreshError?.let { error ->
            UriToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = ScreenMargin, vertical = 12.dp),
                type = UriToastType.Error,
                titleText = localizedString(R.string.explore_refresh_failed),
                subTitleText = localizedString(error.toMessage()),
                onClose = { onAction(DismissRefreshError) }
            )
        }
    }
}

@Composable
private fun ExploreFeed(
    state: ExploreUiState,
    onAction: (ExploreAction) -> Unit
) {
    val gridState = rememberLazyStaggeredGridState()
    val feedState = state.feedState
    val onEntryClick = entryClick(onAction)
    val showDivider by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0
        }
    }

    LoadMoreEffect(
        gridState = gridState,
        page = state.page,
        onLoadNextPage = { onAction(LoadNextPage) }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        KindChips(
            modifier = Modifier
                .padding(horizontal = ScreenMargin)
                .bleed(ScreenMargin),
            selected = state.selectedKind,
            onSelect = { onAction(OnKindSelect(it)) }
        )
        AnimatedVisibility(
            visible = showDivider,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            UriDivider(modifier = Modifier.padding(top = 8.dp))
        }

        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            state = gridState,
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = ScreenMargin,
                end = ScreenMargin,
                top = 16.dp,
                bottom = ScreenMargin + LocalScreenBottomPadding.current
            ),
            horizontalArrangement = Arrangement.spacedBy(FeedSpacing),
            verticalItemSpacing = FeedSpacing
        ) {
            if (state.featured.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine, contentType = "featured") {
                    FeaturedSection(
                        modifier = Modifier.bleed(ScreenMargin),
                        entries = state.featured,
                        onEntryClick = onEntryClick
                    )
                }
            }

            state.regionRows.forEach { row ->
                item(
                    span = StaggeredGridItemSpan.FullLine,
                    key = "region-${row.region}",
                    contentType = "region"
                ) {
                    EntryRow(
                        modifier = Modifier.bleed(ScreenMargin),
                        title = localizedString(R.string.explore_around_region, row.region),
                        subtitle = row.summary(),
                        entries = row.entries,
                        showKind = true,
                        onSeeAll = null,
                        onEntryClick = onEntryClick
                    )
                }
            }

            item(span = StaggeredGridItemSpan.FullLine, contentType = "header") {
                SectionHeader(
                    title = when {
                        state.isSearching -> localizedString(
                            R.string.explore_results_for,
                            state.submittedQuery
                        )

                        state.selectedKind != null -> state.selectedKind.pluralLabel
                        else -> localizedString(R.string.explore_for_you)
                    },
                    subtitle = when {
                        state.isSearching -> state.selectedKind?.let {
                            localizedString(
                                R.string.explore_search_in,
                                it.pluralLabel.lowercase()
                            )
                        }
                            ?: localizedString(R.string.explore_search_everywhere)

                        state.selectedKind != null -> state.selectedKind.tagline
                        else -> localizedString(R.string.explore_for_you_subtitle)
                    }
                )
            }

            items(
                items = state.entries,
                key = { "${it.kind}-${it.id}" },
                contentType = { it.kind }
            ) { entry ->
                CompendiumFeedCard(
                    entry = entry,
                    showKind = state.selectedKind == null,
                    onClick = onEntryClick(entry)
                )
            }

            if (state.failedKinds.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine, contentType = "failed") {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = localizedString(
                            R.string.explore_sections_failed,
                            state.failedKinds.map { it.pluralLabel.lowercase() }.joinToString()
                        ),
                        style = UrithiruTheme.typography.bodyS,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item(span = StaggeredGridItemSpan.FullLine, contentType = "footer") {
                UriListFooter(
                    isLoading = feedState is StateLoading,
                    error = (feedState as? StateFailed)?.failed,
                    onRetry = { onAction(LoadNextPage) }
                )
            }
        }
    }
}

@Composable
private fun SearchResults(
    state: ExploreUiState,
    onAction: (ExploreAction) -> Unit
) {
    val result = state.searchResult ?: return
    val onEntryClick = entryClick(onAction)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = ScreenMargin + LocalScreenBottomPadding.current
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item(contentType = "kinds") {
            KindChips(
                selected = state.selectedKind,
                onSelect = { onAction(OnKindSelect(it)) }
            )
        }

        item(contentType = "header") {
            SectionHeader(
                modifier = Modifier.padding(horizontal = ScreenMargin),
                title = localizedString(R.string.explore_results_for, state.submittedQuery),
                subtitle = (result.groups.map { it.kind } + listOfNotNull(result.topResult?.kind)).distinct().size.let { count ->
                    localizedString(R.string.explore_found_in_sections, count)
                }
            )
        }

        result.topResult?.let { top ->
            item(contentType = "top") {
                Column(
                    modifier = Modifier.padding(horizontal = ScreenMargin),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = localizedString(R.string.explore_top_result),
                        style = UrithiruTheme.typography.h4,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    FeaturedEntryCard(
                        modifier = Modifier.height(200.dp),
                        entry = top,
                        onClick = onEntryClick(top)
                    )
                }
            }
        }

        items(result.groups, key = { it.kind.name }, contentType = { "group" }) { group ->
            EntryRow(
                title = localizedString(
                    if (group.hasMore) R.string.explore_group_title_more else R.string.explore_group_title,
                    group.kind.pluralLabel,
                    group.entries.size
                ),
                entries = group.entries,
                showKind = false,
                onSeeAll = if (group.hasMore) {
                    { onAction(OnKindSelect(group.kind)) }
                } else {
                    null
                },
                onEntryClick = onEntryClick
            )
        }

        if (result.failedKinds.isNotEmpty()) {
            item(contentType = "failed") {
                Text(
                    modifier = Modifier.padding(horizontal = ScreenMargin),
                    text = localizedString(
                        R.string.explore_search_failed,
                        result.failedKinds.map { it.pluralLabel.lowercase() }.joinToString()
                    ),
                    style = UrithiruTheme.typography.bodyS,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EntryRow(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    entries: List<CompendiumEntry>,
    showKind: Boolean,
    onSeeAll: (() -> Unit)?,
    onEntryClick: (CompendiumEntry) -> () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenMargin),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = UrithiruTheme.typography.h4,
                    color = MaterialTheme.colorScheme.onBackground
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = UrithiruTheme.typography.bodyS,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            onSeeAll?.let {
                TextButton(onClick = it) {
                    Text(localizedString(R.string.explore_see_all))
                }
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = ScreenMargin),
            horizontalArrangement = Arrangement.spacedBy(FeedSpacing)
        ) {
            items(entries, key = { "${it.kind}-${it.id}" }) { entry ->
                CompendiumFeedCard(
                    modifier = Modifier.size(width = SearchCardWidth, height = SearchCardHeight),
                    entry = entry,
                    showKind = showKind,
                    uniform = true,
                    onClick = onEntryClick(entry)
                )
            }
        }
    }
}

@Composable
private fun RegionRow.summary(): String =
    entries.groupingBy { it.kind }.eachCount().entries
        .sortedBy { it.key.ordinal }
        .map { (kind, count) -> kind.countLabel(count) }
        .joinToString(localizedString(R.string.explore_summary_separator))

private fun entryClick(onAction: (ExploreAction) -> Unit): (CompendiumEntry) -> () -> Unit =
    { entry ->
        { onAction(OnEntryClick(entry)) }
    }

@Composable
private fun LoadMoreEffect(
    gridState: LazyStaggeredGridState,
    page: Int,
    onLoadNextPage: () -> Unit
) {
    val isNearEnd by remember(gridState) {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val lastVisible =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            lastVisible >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(isNearEnd, page) {
        if (isNearEnd) onLoadNextPage()
    }
}

@Composable
private fun KindChips(
    modifier: Modifier = Modifier,
    selected: CompendiumKind?,
    onSelect: (CompendiumKind?) -> Unit
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = ScreenMargin),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text(localizedString(R.string.explore_all)) },
                shape = CircleShape
            )
        }
        items(CompendiumKind.entries, key = { it.name }) { kind ->
            FilterChip(
                selected = kind == selected,
                onClick = { onSelect(kind) },
                label = { Text(kind.pluralLabel) },
                shape = CircleShape
            )
        }
    }
}

@Composable
private fun FeaturedSection(
    modifier: Modifier = Modifier,
    entries: List<CompendiumEntry>,
    onEntryClick: (CompendiumEntry) -> () -> Unit
) {
    val pagerState = rememberPagerState { entries.size }

    LaunchedEffect(pagerState, entries.size) {
        while (true) {
            delay(FEATURED_AUTO_SCROLL_MS.milliseconds)
            if (!pagerState.isScrollInProgress && entries.size > 1) {
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % entries.size)
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = ScreenMargin),
            title = localizedString(R.string.explore_featured),
            subtitle = localizedString(R.string.explore_featured_subtitle)
        )
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = ScreenMargin),
            pageSpacing = FeedSpacing
        ) { page ->
            FeaturedEntryCard(
                modifier = Modifier
                    .height(200.dp)
                    .pageScale(pagerState, page),
                entry = entries[page],
                onClick = onEntryClick(entries[page])
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
        ) {
            repeat(entries.size) { index ->
                UriDot(
                    isFilled = index == pagerState.currentPage,
                    filledColor = MaterialTheme.colorScheme.primary,
                    unfilledColor = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String
) {
    Column(
        modifier = modifier.padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = UrithiruTheme.typography.h3,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = UrithiruTheme.typography.bodyS,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeedSkeleton(
    selectedKind: CompendiumKind?,
    showFeatured: Boolean,
    onSelectKind: (CompendiumKind?) -> Unit
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = ScreenMargin,
            end = ScreenMargin,
            top = 16.dp,
            bottom = ScreenMargin + LocalScreenBottomPadding.current
        ),
        horizontalArrangement = Arrangement.spacedBy(FeedSpacing),
        verticalItemSpacing = FeedSpacing,
        userScrollEnabled = false
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            KindChips(
                modifier = Modifier.bleed(ScreenMargin),
                selected = selectedKind,
                onSelect = onSelectKind
            )
        }
        if (showFeatured) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HeaderSkeleton()
                    UriSkeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )
                }
            }
        }
        item(span = StaggeredGridItemSpan.FullLine) {
            HeaderSkeleton()
        }
        items(SkeletonCardRatios) { ratio ->
            CardSkeleton(ratio = ratio)
        }
    }
}

@Composable
private fun HeaderSkeleton() {
    Column(
        modifier = Modifier.padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UriSkeleton(
            modifier = Modifier
                .size(width = 120.dp, height = 22.dp)
                .clip(RoundedCornerShape(6.dp))
        )
        UriSkeleton(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(14.dp)
                .clip(RoundedCornerShape(6.dp))
        )
    }
}

@Composable
private fun CardSkeleton(ratio: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        UriSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio)
                .clip(RoundedCornerShape(14.dp))
        )
        Column(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UriSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
            UriSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
        }
    }
}
