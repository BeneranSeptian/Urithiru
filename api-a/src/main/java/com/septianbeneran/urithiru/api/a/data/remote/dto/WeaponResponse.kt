package com.septianbeneran.urithiru.api.a.data.remote.dto

import com.septianbeneran.urithiru.api.a.data.remote.dto.common.AttributesResponse
import com.septianbeneran.urithiru.api.a.data.remote.dto.common.ScalingResponse
import com.septianbeneran.urithiru.core.entity.a.Weapon
import kotlinx.serialization.Serializable

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