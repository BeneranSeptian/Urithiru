package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.common.StatValueResponse
import com.septianbeneran.urithiru.core.entity.eldenring.Armor
import com.septianbeneran.urithiru.core.util.extension.cleaned
import kotlinx.serialization.Serializable

@Serializable
data class ArmorResponse(
    val id: String,
    val name: String,
    val image: String? = null,
    val description: String? = null,
    val category: String? = null,
    val dmgNegation: List<StatValueResponse> = emptyList(),
    val resistance: List<StatValueResponse> = emptyList(),
    val weight: Float? = null
) : CompendiumResponse {
    override fun mapToEntry() = Armor(
        id = id,
        name = name.trim(),
        image = image,
        description = description.cleaned(),
        category = category.cleaned(),
        dmgNegation = dmgNegation.map { it.mapToStatValue() },
        resistance = resistance.map { it.mapToStatValue() },
        weight = weight
    )
}
