package com.septianbeneran.urithiru.feature.explore.screen.stateaction

import com.septianbeneran.urithiru.core.entity.eldenring.Boss
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.Item
import com.septianbeneran.urithiru.core.entity.eldenring.Location
import org.junit.Assert.assertEquals
import org.junit.Test

class ExploreUiStateTest {

    @Test
    fun groupsBossesAndLocationsByRegionLargestFirstAndOnlyOnceARowIsFull() {
        val state = ExploreUiState(
            featured = listOf(boss("b0", "Limgrave")),
            entries = List(3) { boss("b${it + 1}", "Limgrave") } +
                List(5) { location("l$it", "Caelid") } +
                List(3) { boss("w$it", "Weeping Peninsula") } +
                List(6) { item("i$it") }
        )

        val rows = state.regionRows

        assertEquals(listOf("Caelid", "Limgrave"), rows.map { it.region })
        assertEquals(listOf("b0", "b1", "b2", "b3"), rows.last().entries.map { it.id })
    }

    @Test
    fun showsAtMostTwoRegions() {
        val state = ExploreUiState(
            entries = listOf("Limgrave", "Caelid", "Altus Plateau").flatMap { region ->
                List(4) { location("$region-$it", region) }
            }
        )

        assertEquals(2, state.regionRows.size)
    }

    @Test
    fun hidesRegionRowsWhileSearchingOrInsideOneSection() {
        val entries = List(4) { boss("b$it", "Limgrave") }

        assertEquals(emptyList<RegionRow>(), ExploreUiState(entries = entries, submittedQuery = "god").regionRows)
        assertEquals(emptyList<RegionRow>(), ExploreUiState(entries = entries, selectedKind = CompendiumKind.Bosses).regionRows)
    }

    private fun boss(id: String, region: String) = Boss(
        id = id,
        name = id,
        image = null,
        description = null,
        region = region,
        location = null,
        drops = emptyList(),
        healthPoints = null
    )

    private fun location(id: String, region: String) =
        Location(id = id, name = id, image = null, description = null, region = region)

    private fun item(id: String) =
        Item(id = id, name = id, image = null, description = null, type = null, effect = null)
}
