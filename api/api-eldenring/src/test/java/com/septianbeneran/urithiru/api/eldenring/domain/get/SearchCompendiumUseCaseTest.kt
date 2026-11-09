package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.api.eldenring.repository.CompendiumRepository
import com.septianbeneran.urithiru.core.entity.eldenring.Boss
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind.Armors
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind.Bosses
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind.Items
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind.Weapons
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumSearchResult
import com.septianbeneran.urithiru.core.entity.eldenring.Item
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchCompendiumUseCaseTest {

    @Test
    fun groupsBySectionWithTheClosestMatchFirst() = runBlocking {
        val repo = FakeRepository(
            mapOf(
                Bosses to page(boss("Malenia, Blade Of Miquella")),
                Weapons to page(item("Greatsword"), item("Hand Of Malenia")),
                Armors to page(item("Malenia's Armor"), item("Malenia's Gauntlet")),
                Items to page()
            )
        )

        val emissions = SearchCompendiumUseCaseImpl(repo)("malenia", kinds = setOf(Bosses, Weapons, Armors, Items)).toList()
        val result = emissions.last().successData()

        assertTrue(emissions.first() is ApiResult.Loading)
        assertEquals(listOf(Armors, Weapons), result.groups.map { it.kind })
        assertEquals(listOf("Hand Of Malenia", "Greatsword"), result.groups.last().entries.map { it.name })
        assertEquals("Malenia, Blade Of Miquella", result.topResult?.name)
    }

    @Test
    fun theTopResultIsTakenOutOfItsGroupSoItShowsOnce() = runBlocking {
        val repo = FakeRepository(mapOf(Bosses to page(boss("Rennala"), boss("Rennala, Queen Of The Full Moon"))))

        val result = SearchCompendiumUseCaseImpl(repo)("rennala", kinds = setOf(Bosses)).last().successData()

        assertEquals("Rennala", result.topResult?.name)
        assertEquals(listOf("Rennala, Queen Of The Full Moon"), result.groups.single().entries.map { it.name })
    }

    @Test
    fun hasNoTopResultWhenNothingStartsWithTheQuery() = runBlocking {
        val repo = FakeRepository(mapOf(Weapons to page(item("Greatsword"), item("Hand Of Malenia"))))

        val result = SearchCompendiumUseCaseImpl(repo)("malenia", kinds = setOf(Weapons)).last().successData()

        assertNull(result.topResult)
    }

    @Test
    fun aFullPageMeansTheSectionMayHaveMore() = runBlocking {
        val repo = FakeRepository(
            mapOf(
                Weapons to page(*Array(3) { item("Axe $it") }),
                Armors to page(item("Axe Helm"))
            )
        )

        val result = SearchCompendiumUseCaseImpl(repo)("axe", kinds = setOf(Weapons, Armors), perKind = 3).last().successData()

        assertEquals(mapOf(Weapons to true, Armors to false), result.groups.associate { it.kind to it.hasMore })
    }

    @Test
    fun searchesTheFirstPageOfEverySectionWithTheTrimmedQuery() = runBlocking {
        val repo = FakeRepository(mapOf(Bosses to page(), Items to page()))

        SearchCompendiumUseCaseImpl(repo)("  Rennala ", kinds = setOf(Bosses, Items), perKind = 5).last()

        assertEquals(setOf(Request(Bosses, 5, "Rennala", 0), Request(Items, 5, "Rennala", 0)), repo.requests.toSet())
    }

    @Test
    fun aFailingSectionIsReportedAndTheRestStillShow() = runBlocking {
        val repo = FakeRepository(
            mapOf(Bosses to ApiResult.Error(AppError.Timeout), Weapons to page(item("Great Hand Axe")))
        )

        val result = SearchCompendiumUseCaseImpl(repo)("hand", kinds = setOf(Bosses, Weapons)).last().successData()

        assertEquals(listOf(Weapons), result.groups.map { it.kind })
        assertEquals(setOf(Bosses), result.failedKinds)
    }

    @Test
    fun failsOnlyWhenEverySectionFails() = runBlocking {
        val repo = FakeRepository(
            mapOf(Bosses to ApiResult.Error(AppError.Timeout), Weapons to ApiResult.Error(AppError.NoInternet))
        )

        val result = SearchCompendiumUseCaseImpl(repo)("hand", kinds = setOf(Bosses, Weapons)).last()

        assertTrue(result is ApiResult.Error)
    }

    @Test
    fun matchScoreRanksExactThenPrefixThenWordThenContainsThenFuzzy() {
        val ranked = listOf("Greatsword", "Zweihander", "Hand Of Malenia", "Hand", "Malenia's Hand")
            .sortedBy { matchScore(it, "hand") }

        assertEquals(listOf("Hand", "Hand Of Malenia", "Malenia's Hand", "Zweihander", "Greatsword"), ranked)
    }

    private fun page(vararg entries: CompendiumEntry): ApiResult<CompendiumPage> =
        ApiResult.Success(CompendiumPage(entries.toList(), null))

    private fun ApiResult<CompendiumSearchResult>.successData(): CompendiumSearchResult =
        (this as ApiResult.Success).data!!

    private data class Request(val kind: CompendiumKind, val limit: Int?, val name: String?, val page: Int?)

    private class FakeRepository(
        private val results: Map<CompendiumKind, ApiResult<CompendiumPage>>
    ) : CompendiumRepository {
        val requests = mutableListOf<Request>()

        override fun getList(kind: CompendiumKind, limit: Int?, name: String?, page: Int?): Flow<ApiResult<CompendiumPage>> {
            synchronized(requests) { requests += Request(kind, limit, name, page) }
            return flowOf(ApiResult.Loading(), results.getValue(kind))
        }

        override fun getDetail(kind: CompendiumKind, id: String): Flow<ApiResult<CompendiumEntry>> =
            error("Not used by search")
    }

    private fun boss(name: String) =
        Boss(id = name, name = name, image = null, description = null, region = null, location = null, drops = emptyList(), healthPoints = null)

    private fun item(name: String) =
        Item(id = name, name = name, image = null, description = null, type = null, effect = null)
}
