package com.septianbeneran.urithiru.feature.explore.viewmodel

import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumFeedPageUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumListUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.SearchCompendiumUseCase
import com.septianbeneran.urithiru.core.base.BaseState
import com.septianbeneran.urithiru.core.entity.eldenring.Boss
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumFeedPage
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind.Bosses
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind.Items
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumSearchResult
import com.septianbeneran.urithiru.core.entity.eldenring.Item
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreAction
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.ExploreNonce
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {

    private val feed = FakeFeed()
    private val list = FakeList()
    private val search = FakeSearch()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun opensWithTheMixedFeedAndOneFeaturedEntryPerSection() {
        feed.next = { success(boss("b1", featured = true), boss("b2", featured = true), item("i1")) }

        val state = viewModel().uiState.value

        assertEquals(listOf("b1"), state.featured.map { it.id })
        assertEquals(listOf("b2", "i1"), state.entries.map { it.id })
        assertTrue(state.feedState is BaseState.StateSuccess)
        assertEquals(CompendiumKind.entries.associateWith { 0 }, feed.requests.single().pages)
    }

    @Test
    fun aFailedSectionKeepsItsPageSoTheNextLoadRetriesIt() {
        feed.next = { success(item("i1"), failed = setOf(Bosses)) }
        val viewModel = viewModel()

        assertEquals(setOf(Bosses), viewModel.uiState.value.failedKinds)

        feed.next = { success(boss("b1"), item("i2")) }
        viewModel.onAction(ExploreAction.LoadNextPage)

        val pages = feed.requests.last().pages
        assertEquals(0, pages[Bosses])
        assertEquals(1, pages[Items])
        assertEquals(emptySet<CompendiumKind>(), viewModel.uiState.value.failedKinds)
    }

    @Test
    fun anEndedSectionIsNotAskedAgainAndTheFeedEndsWhenAllHaveEnded() {
        feed.next = { success(boss("b1"), ended = setOf(Bosses)) }
        val viewModel = viewModel()

        feed.next = { success(item("i1"), ended = CompendiumKind.entries.toSet() - Bosses) }
        viewModel.onAction(ExploreAction.LoadNextPage)

        assertFalse(Bosses in feed.requests.last().pages)
        assertTrue(viewModel.uiState.value.isEndReached)
    }

    @Test
    fun pullToRefreshKeepsTheFeedVisibleThenReplacesItWithANewMix() {
        feed.next = { success(item("old")) }
        val viewModel = viewModel()
        val gate = CompletableDeferred<ApiResult<CompendiumFeedPage>>()
        feed.next = { flow { emit(ApiResult.Loading()); emit(gate.await()) } }

        viewModel.onAction(ExploreAction.Refresh)

        assertTrue(viewModel.uiState.value.isRefreshing)
        assertEquals(listOf("old"), viewModel.uiState.value.entries.map { it.id })

        gate.complete(ApiResult.Success(feedPage(item("new"))))

        assertFalse(viewModel.uiState.value.isRefreshing)
        assertEquals(listOf("new"), viewModel.uiState.value.entries.map { it.id })
        assertEquals(CompendiumKind.entries.associateWith { 0 }, feed.requests.last().pages)
        assertNotEquals(feed.requests.first().seed, feed.requests.last().seed)
    }

    @Test
    fun aFailedRefreshKeepsTheOldFeedAndShowsTheErrorUntilDismissed() {
        feed.next = { success(item("old")) }
        val viewModel = viewModel()

        feed.next = { flowOf(ApiResult.Loading(), ApiResult.Error(AppError.Timeout)) }
        viewModel.onAction(ExploreAction.Refresh)

        val state = viewModel.uiState.value
        assertEquals(listOf("old"), state.entries.map { it.id })
        assertEquals(AppError.Timeout, state.refreshError)
        assertFalse(state.isRefreshing)

        viewModel.onAction(ExploreAction.DismissRefreshError)
        assertNull(viewModel.uiState.value.refreshError)
    }

    @Test
    fun searchingOnAllShowsGroupedResultsEvenWhenOnlyATopResultMatched() {
        feed.next = { success(item("i1")) }
        search.next = { CompendiumSearchResult(topResult = item("Hand Of Malenia"), groups = emptyList(), failedKinds = emptySet()) }
        val viewModel = viewModel()

        viewModel.onAction(ExploreAction.OnSearch("  Hand Of Malenia "))

        val state = viewModel.uiState.value
        assertTrue(state.isGroupedSearch)
        assertTrue(state.hasContent)
        assertEquals("Hand Of Malenia", search.queries.single())
    }

    @Test
    fun choosingASectionLoadsItAloneAndStopsOnAShortPage() {
        feed.next = { success(item("i1")) }
        list.next = { CompendiumPage(List(3) { boss("b$it") }, total = 106) }
        val viewModel = viewModel()

        viewModel.onAction(ExploreAction.OnKindSelect(Bosses))

        assertEquals(Bosses to 0, list.requests.single())
        assertEquals(3, viewModel.uiState.value.entries.size + viewModel.uiState.value.featured.size)
        assertTrue(viewModel.uiState.value.isEndReached)
    }

    @Test
    fun tappingAnEntryOpensItsDetail() = runTest {
        feed.next = { success(boss("b1")) }
        val viewModel = viewModel()

        viewModel.onAction(ExploreAction.OnEntryClick(boss("b1")))

        assertEquals(ExploreNonce.NavigateToDetail(Bosses, "b1"), viewModel.nonce.first())
    }

    private fun viewModel() = ExploreViewModel(feed, list, search)

    private fun success(
        vararg entries: CompendiumEntry,
        ended: Set<CompendiumKind> = emptySet(),
        failed: Set<CompendiumKind> = emptySet()
    ): Flow<ApiResult<CompendiumFeedPage>> =
        flowOf(ApiResult.Loading(), ApiResult.Success(feedPage(*entries, ended = ended, failed = failed)))

    private fun feedPage(
        vararg entries: CompendiumEntry,
        ended: Set<CompendiumKind> = emptySet(),
        failed: Set<CompendiumKind> = emptySet()
    ) = CompendiumFeedPage(entries = entries.toList(), endedKinds = ended, failedKinds = failed)

    private fun boss(id: String, featured: Boolean = false) = Boss(
        id = id,
        name = id,
        image = if (featured) "image.png" else null,
        description = if (featured) "A boss." else null,
        region = null,
        location = null,
        drops = emptyList(),
        healthPoints = null
    )

    private fun item(id: String) =
        Item(id = id, name = id, image = null, description = null, type = null, effect = null)

    private data class FeedRequest(val pages: Map<CompendiumKind, Int>, val seed: Long)

    private class FakeFeed : GetCompendiumFeedPageUseCase {
        var next: () -> Flow<ApiResult<CompendiumFeedPage>> = { flowOf() }
        val requests = mutableListOf<FeedRequest>()

        override fun invoke(
            pages: Map<CompendiumKind, Int>,
            seed: Long,
            name: String?,
            perKind: Int
        ): Flow<ApiResult<CompendiumFeedPage>> {
            requests += FeedRequest(pages, seed)
            return next()
        }
    }

    private class FakeList : GetCompendiumListUseCase {
        var next: () -> CompendiumPage = { CompendiumPage(emptyList(), null) }
        val requests = mutableListOf<Pair<CompendiumKind, Int?>>()

        override fun invoke(kind: CompendiumKind, limit: Int?, name: String?, page: Int?): Flow<ApiResult<CompendiumPage>> {
            requests += kind to page
            return flowOf(ApiResult.Loading(), ApiResult.Success(next()))
        }
    }

    private class FakeSearch : SearchCompendiumUseCase {
        var next: () -> CompendiumSearchResult = { CompendiumSearchResult(null, emptyList(), emptySet()) }
        val queries = mutableListOf<String>()

        override fun invoke(query: String, kinds: Set<CompendiumKind>, perKind: Int): Flow<ApiResult<CompendiumSearchResult>> {
            queries += query
            return flowOf(ApiResult.Loading(), ApiResult.Success(next()))
        }
    }
}
