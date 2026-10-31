package com.septianbeneran.urithiru.feature.explore.screen

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
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
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_explore.ExploreRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_explore.CompendiumDetailRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriDot
import com.septianbeneran.urithiru.core.ui.component.UriEmptyState
import com.septianbeneran.urithiru.core.ui.component.UriErrorState
import com.septianbeneran.urithiru.core.ui.component.UriListFooter
import com.septianbeneran.urithiru.core.ui.component.UriSearchBar
import com.septianbeneran.urithiru.core.ui.component.UriSkeleton
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.util.NonceObserver
import com.septianbeneran.urithiru.feature.explore.screen.feed.CompendiumFeedCard
import com.septianbeneran.urithiru.feature.explore.screen.feed.FeaturedEntryCard
import com.septianbeneran.urithiru.feature.explore.screen.feed.pluralLabel
import com.septianbeneran.urithiru.feature.explore.screen.feed.tagline
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
import com.septianbeneran.urithiru.feature.explore.viewmodel.ExploreViewModel
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

private val ScreenMargin = 24.dp
private val FeedSpacing = 12.dp
private val SearchCardWidth = 168.dp
private val SearchCardHeight = 240.dp
private const val FEATURED_AUTO_SCROLL_MS = 4_000L
private const val LOAD_MORE_THRESHOLD = 4
private val SkeletonCardRatios = listOf(0.78f, 1f, 1.15f, 0.9f)

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
        topAppBarArgs = defaults(title = "Explore", backIcon = null)
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
                is NavigateToDetail -> navigator.navigate(CompendiumDetailRoute(kind = nonce.kind.name, id = nonce.id))
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
            placeholder = "Search bosses, gear, places…",
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
                        title = "Nothing found",
                        message = if (uiState.isSearching) "Try a different name." else "Try another section."
                    )
                }
            }

            uiState.isGroupedSearch -> SearchResults(
                state = uiState,
                onAction = onAction
            )

            else -> ExploreFeed(
                state = uiState,
                onAction = onAction
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

    LoadMoreEffect(
        gridState = gridState,
        page = state.page,
        onLoadNextPage = { onAction(LoadNextPage) }
    )

    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        state = gridState,
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(start = ScreenMargin, end = ScreenMargin, top = 16.dp, bottom = ScreenMargin),
        horizontalArrangement = Arrangement.spacedBy(FeedSpacing),
        verticalItemSpacing = FeedSpacing
    ) {
        item(span = StaggeredGridItemSpan.FullLine, contentType = "kinds") {
            KindChips(
                modifier = Modifier.bleed(ScreenMargin),
                selected = state.selectedKind,
                onSelect = { onAction(OnKindSelect(it)) }
            )
        }

        if (state.featured.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine, contentType = "featured") {
                FeaturedSection(
                    modifier = Modifier.bleed(ScreenMargin),
                    entries = state.featured,
                    onEntryClick = onEntryClick
                )
            }
        }

        item(span = StaggeredGridItemSpan.FullLine, contentType = "header") {
            SectionHeader(
                title = when {
                    state.isSearching -> "Results for \"${state.submittedQuery}\""
                    state.selectedKind != null -> state.selectedKind.pluralLabel
                    else -> "For you"
                },
                subtitle = when {
                    state.isSearching -> state.selectedKind?.let { "In ${it.pluralLabel.lowercase()}" } ?: "Across the whole compendium"
                    state.selectedKind != null -> state.selectedKind.tagline
                    else -> "Bosses, gear, places and people of the Lands Between"
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

        item(span = StaggeredGridItemSpan.FullLine, contentType = "footer") {
            UriListFooter(
                isLoading = feedState is StateLoading,
                error = (feedState as? StateFailed)?.failed,
                onRetry = { onAction(LoadNextPage) }
            )
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
        contentPadding = PaddingValues(top = 16.dp, bottom = ScreenMargin),
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
                title = "Results for \"${state.submittedQuery}\"",
                subtitle = when (val count = (result.groups.map { it.kind } + listOfNotNull(result.topResult?.kind)).distinct().size) {
                    1 -> "Found in 1 section"
                    else -> "Found in $count sections"
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
                        text = "Top result",
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenMargin),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${group.kind.pluralLabel} · ${group.entries.size}${if (group.hasMore) "+" else ""}",
                        style = UrithiruTheme.typography.h4,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (group.hasMore) {
                        TextButton(onClick = { onAction(OnKindSelect(group.kind)) }) {
                            Text("See all")
                        }
                    }
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = ScreenMargin),
                    horizontalArrangement = Arrangement.spacedBy(FeedSpacing)
                ) {
                    items(group.entries, key = { it.id }) { entry ->
                        CompendiumFeedCard(
                            modifier = Modifier.size(width = SearchCardWidth, height = SearchCardHeight),
                            entry = entry,
                            showKind = false,
                            uniform = true,
                            onClick = onEntryClick(entry)
                        )
                    }
                }
            }
        }

        if (result.failedKinds.isNotEmpty()) {
            item(contentType = "failed") {
                Text(
                    modifier = Modifier.padding(horizontal = ScreenMargin),
                    text = "Couldn't search ${result.failedKinds.joinToString { it.pluralLabel.lowercase() }} this time.",
                    style = UrithiruTheme.typography.bodyS,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun entryClick(onAction: (ExploreAction) -> Unit): (CompendiumEntry) -> () -> Unit = { entry ->
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
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
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
                label = { Text("All") },
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
            delay(FEATURED_AUTO_SCROLL_MS)
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
            title = "Featured",
            subtitle = "Legendary picks worth a closer look"
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
        contentPadding = PaddingValues(start = ScreenMargin, end = ScreenMargin, top = 16.dp, bottom = ScreenMargin),
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

private fun Modifier.bleed(horizontal: Dp): Modifier = layout { measurable, constraints ->
    val extra = horizontal.roundToPx()
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = constraints.minWidth + extra * 2,
            maxWidth = constraints.maxWidth + extra * 2
        )
    )
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(-extra, 0)
    }
}

private fun Modifier.pageScale(pagerState: PagerState, page: Int): Modifier = graphicsLayer {
    val offset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
    val scale = 1f - 0.08f * offset.coerceIn(0f, 1f)
    scaleX = scale
    scaleY = scale
}
