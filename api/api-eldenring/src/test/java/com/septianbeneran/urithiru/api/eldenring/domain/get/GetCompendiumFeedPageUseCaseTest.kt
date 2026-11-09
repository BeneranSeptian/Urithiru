package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.api.eldenring.repository.CompendiumRepository
import com.septianbeneran.urithiru.core.entity.eldenring.Boss
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumFeedPage
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind.Bosses
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind.Items
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind.Locations
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.entity.eldenring.Item
import com.septianbeneran.urithiru.core.entity.eldenring.Location
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GetCompendiumFeedPageUseCaseTest {

    private val bosses = List(6) { boss("b$it") }
    private val items = List(6) { item("i$it") }
    private val locations = List(6) { location("l$it") }

    @Test
    fun emitsLoadingThenOneMixedPageWithEverySection() = runBlocking {
        val useCase = useCase(Bosses to page(bosses), Items to page(items), Locations to page(locations))

        val emissions = useCase(pages = setOf(Bosses, Items, Locations).associateWith { 0 }, seed = 1L).toList()

        assertTrue(emissions.first() is ApiResult.Loading)
        val feed = emissions.last().successData()
        assertEquals((bosses + items + locations).toSet(), feed.entries.toSet())
        assertEquals(18, feed.entries.size)
        assertEquals(emptySet<CompendiumKind>(), feed.failedKinds)
    }

    @Test
    fun neverPutsTwoEntriesOfTheSameSectionNextToEachOtherWhileOthersRemain() = runBlocking {
        val useCase = useCase(Bosses to page(bosses), Items to page(items), Locations to page(locations))

        repeat(20) { seed ->
            val entries = useCase(pages = setOf(Bosses, Items, Locations).associateWith { 0 }, seed = seed.toLong()).last().successData().entries

            entries.zipWithNext().forEach { (first, second) ->
                assertNotEquals("seed $seed: ${first.id} then ${second.id}", first.kind, second.kind)
            }
        }
    }

    @Test
    fun sameSeedAndPageGiveTheSameOrder() = runBlocking {
        val useCase = useCase(Bosses to page(bosses), Items to page(items), Locations to page(locations))
        val kinds = setOf(Bosses, Items, Locations)

        val first = useCase(pages = kinds.associateWith { 0 }, seed = 7L).last().successData().entries
        val again = useCase(pages = kinds.reversed().toSet().associateWith { 0 }, seed = 7L).last().successData().entries
        val reshuffled = (1L..10L).map { useCase(pages = kinds.associateWith { 0 }, seed = it).last().successData().entries }

        assertEquals(first, again)
        assertTrue(reshuffled.any { it != first })
    }

    @Test
    fun aFailingSectionIsReportedButTheRestStillLoad() = runBlocking {
        val useCase = useCase(Bosses to page(bosses), Items to ApiResult.Error(AppError.Timeout))

        val feed = useCase(pages = setOf(Bosses, Items).associateWith { 0 }, seed = 1L).last().successData()

        assertEquals(bosses, feed.entries)
        assertEquals(setOf(Items), feed.failedKinds)
    }

    @Test
    fun failsOnlyWhenEverySectionFails() = runBlocking {
        val useCase = useCase(Bosses to ApiResult.Error(AppError.NoInternet), Items to ApiResult.Error(AppError.Timeout))

        val result = useCase(pages = setOf(Bosses, Items).associateWith { 0 }, seed = 1L).last()

        assertTrue(result is ApiResult.Error)
    }

    @Test
    fun aShortPageOrAReachedTotalEndsTheSection() = runBlocking {
        val useCase = useCase(
            Bosses to page(bosses.take(3)),
            Items to page(items, total = 12),
            Locations to page(locations, total = 100)
        )

        val feed = useCase(pages = setOf(Bosses, Items, Locations).associateWith { 1 }, seed = 1L).last().successData()

        assertEquals(setOf(Bosses, Items), feed.endedKinds)
    }

    @Test
    fun asksEachSectionForTheSamePageSizeAndName() = runBlocking {
        val repo = FakeRepository(mapOf(Bosses to page(bosses), Items to page(items)))

        GetCompendiumFeedPageUseCaseImpl(repo)(pages = setOf(Bosses, Items).associateWith { 3 }, seed = 1L, name = "Axe", perKind = 4).last()

        assertEquals(setOf(Request(Bosses, 4, "Axe", 3), Request(Items, 4, "Axe", 3)), repo.requests.toSet())
    }

    @Test
    fun eachSectionIsAskedForItsOwnPageAndEndsByItsOwnTotal() = runBlocking {
        val repo = FakeRepository(mapOf(Bosses to page(bosses, total = 18), Items to page(items, total = 18)))

        val feed = GetCompendiumFeedPageUseCaseImpl(repo)(pages = mapOf(Bosses to 2, Items to 0), seed = 1L).last().successData()

        assertEquals(setOf(Request(Bosses, 6, null, 2), Request(Items, 6, null, 0)), repo.requests.toSet())
        assertEquals(setOf(Bosses), feed.endedKinds)
    }

    @Test
    fun mixFeedDrainsUnevenSectionsWithoutLosingEntries() {
        val mixed = mixFeed(
            byKind = mapOf(Bosses to bosses.take(1), Items to items, Locations to emptyList()),
            random = Random(3)
        )

        assertEquals(7, mixed.size)
        assertEquals((bosses.take(1) + items).toSet(), mixed.toSet())
    }

    private fun useCase(vararg results: Pair<CompendiumKind, ApiResult<CompendiumPage>>) =
        GetCompendiumFeedPageUseCaseImpl(FakeRepository(results.toMap()))

    private fun page(entries: List<CompendiumEntry>, total: Int? = null): ApiResult<CompendiumPage> =
        ApiResult.Success(CompendiumPage(entries, total))

    private fun ApiResult<CompendiumFeedPage>.successData(): CompendiumFeedPage =
        (this as ApiResult.Success).data!!

    private class FakeRepository(
        private val results: Map<CompendiumKind, ApiResult<CompendiumPage>>
    ) : CompendiumRepository {
        val requests = mutableListOf<Request>()

        override fun getList(kind: CompendiumKind, limit: Int?, name: String?, page: Int?): Flow<ApiResult<CompendiumPage>> {
            synchronized(requests) { requests += Request(kind, limit, name, page) }
            return flowOf(ApiResult.Loading(), results.getValue(kind))
        }

        override fun getDetail(kind: CompendiumKind, id: String): Flow<ApiResult<CompendiumEntry>> =
            error("Not used by the feed")
    }

    private data class Request(val kind: CompendiumKind, val limit: Int?, val name: String?, val page: Int?)

    private fun boss(id: String) =
        Boss(id = id, name = id, image = null, description = null, region = null, location = null, drops = emptyList(), healthPoints = null)

    private fun item(id: String) =
        Item(id = id, name = id, image = null, description = null, type = null, effect = null)

    private fun location(id: String) =
        Location(id = id, name = id, image = null, description = null, region = null)
}
