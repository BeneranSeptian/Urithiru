package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

import com.septianbeneran.urithiru.core.entity.eldenring.Boss
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BossResponseTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun decodesTheBossListEnvelopeWithTotal() {
        val body = """
            {"success":true,"count":1,"total":106,"data":[{"id":"17f695abef9l0i1ujzrup5y0z7rw6",
            "name":"Abductor Virgins","image":null,"region":"Mount Gelmir",
            "description":"Deadly mechanical constructs who wield massive blades.","location":"Volcano Manor",
            "drops":["10.000 Runes","Inquisitor's Girandole"],"healthPoints":"???"}]}
        """.trimIndent()

        val dto = json.decodeFromString<ApiDto<List<BossResponse>>>(body)
        val boss = dto.data.single().mapToEntry()

        assertEquals(106, dto.total)
        assertEquals("Abductor Virgins", boss.name)
        assertEquals("Mount Gelmir", boss.region)
        assertEquals(listOf("10.000 Runes", "Inquisitor's Girandole"), boss.drops)
        assertNull(boss.image)
        assertNull(boss.healthPoints)
        assertEquals(CompendiumKind.Bosses, boss.kind)
    }

    @Test
    fun mapsPlaceholdersAndMissingFieldsToNull() {
        val boss = json.decodeFromString<BossResponse>(
            """{"id":"1","name":"  Tree Sentinel ","location":" ","drops":["None"," Golden Halberd "]}"""
        ).mapToEntry()

        assertEquals(
            Boss(
                id = "1",
                name = "Tree Sentinel",
                image = null,
                description = null,
                region = null,
                location = null,
                drops = listOf("Golden Halberd"),
                healthPoints = null
            ),
            boss
        )
    }
}
