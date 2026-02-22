package com.septianbeneran.template.api_a.data.remote.dto

import com.septianbeneran.template.core_entity.a.Weapon
import com.septianbeneran.template.api_a.data.remote.dto.common.AttributesResponse
import com.septianbeneran.template.api_a.data.remote.dto.common.ScalingResponse
import kotlinx.serialization.Serializable
import kotlin.String

@Serializable
data class WeaponResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val image: String? = null,
    val attack: List<AttributesResponse>,
    val defence: List<AttributesResponse>,
    val scalesWith: List<ScalingResponse>,
    val requiredAttributes: List<AttributesResponse>,
    val category: String,
    val weight: Float
) {
    fun mapToWeapon() = Weapon(
        id = id,
        name = name,
        description = description,
        image = image,
        attack = attack.map { it.mapToAttributes() },
        defence = defence.map { it.mapToAttributes() },
        scalesWith = scalesWith.map { it.mapToScaling() },
        requiredAttributes = requiredAttributes.map { it.mapToAttributes() },
        category = category,
        weight = weight
    )
}