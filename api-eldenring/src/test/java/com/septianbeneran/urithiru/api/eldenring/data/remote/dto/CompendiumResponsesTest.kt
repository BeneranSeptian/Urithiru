package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

import com.septianbeneran.urithiru.core.entity.eldenring.Armor
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.Item
import com.septianbeneran.urithiru.core.entity.eldenring.Location
import com.septianbeneran.urithiru.core.entity.eldenring.Npc
import com.septianbeneran.urithiru.core.entity.eldenring.common.StatValue
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CompendiumResponsesTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun armorKeepsFractionalStats() {
        val armor = json.decodeFromString<ArmorResponse>(
            """{"id":"a","name":"All-knowing Greaves","image":"img.png","description":"Greaves set with countless eyes.",
            "category":"Leg Armor","dmgNegation":[{"name":"Phy","amount":7},{"name":"Slash","amount":2.1}],
            "resistance":[{"name":"Immunity","amount":17}],"weight":6.6}"""
        ).mapToEntry()

        assertEquals("Leg Armor", armor.category)
        assertEquals(listOf(StatValue("Phy", 7f), StatValue("Slash", 2.1f)), armor.dmgNegation)
        assertEquals(listOf(StatValue("Immunity", 17f)), armor.resistance)
        assertEquals(6.6f, armor.weight)
        assertEquals(CompendiumKind.Armors, armor.kind)
    }

    @Test
    fun itemMapsTypeAndEffect() {
        val item = json.decodeFromString<ItemResponse>(
            """{"id":"i","name":"Blue Cipher Ring","image":"img.png","description":"Item for online play.",
            "type":"Reusable","effect":"Enables the wearer to answer calls for rescue"}"""
        ).mapToEntry()

        assertEquals(
            Item(
                id = "i",
                name = "Blue Cipher Ring",
                image = "img.png",
                description = "Item for online play.",
                type = "Reusable",
                effect = "Enables the wearer to answer calls for rescue"
            ),
            item
        )
        assertEquals(CompendiumKind.Items, item.kind)
    }

    @Test
    fun locationIgnoresStrayKeysAndMissingImage() {
        val dto = json.decodeFromString<ApiDto<List<LocationResponse>>>(
            """{"success":true,"count":1,"total":177,"data":[{"id":"l","name":"Bridge Of Sacrifice","image":null,
            "region":"Limgrave","description":"A busy bridge.","Liurnia of the Lakes":"stray"}]}"""
        )

        assertEquals(
            Location(id = "l", name = "Bridge Of Sacrifice", image = null, description = "A busy bridge.", region = "Limgrave"),
            dto.data.single().mapToEntry()
        )
        assertEquals(177, dto.total)
    }

    @Test
    fun npcTrimsRoleAndHasNoDescription() {
        val npc = json.decodeFromString<NpcResponse>(
            """{"id":"n","name":"Merchant Kalé","image":"img.png","quote":null,
            "location":"Limgrave, Church of Elleh","role":" General Goods Shop"}"""
        ).mapToEntry()

        assertEquals(
            Npc(
                id = "n",
                name = "Merchant Kalé",
                image = "img.png",
                quote = null,
                location = "Limgrave, Church of Elleh",
                role = "General Goods Shop"
            ),
            npc
        )
        assertNull(npc.description)
        assertEquals(CompendiumKind.Npcs, npc.kind)
    }

    @Test
    fun armorWithoutStatsDecodesToEmptyLists() {
        val armor: Armor = json.decodeFromString<ArmorResponse>("""{"id":"a","name":"Rags"}""").mapToEntry()

        assertEquals(emptyList<StatValue>(), armor.dmgNegation)
        assertNull(armor.weight)
        assertNull(armor.category)
    }
}
