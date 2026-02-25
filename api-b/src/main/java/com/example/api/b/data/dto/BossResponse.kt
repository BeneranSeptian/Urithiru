package com.example.api.b.data.dto

import com.septianbeneran.template.core.entity.b.Boss
import kotlinx.serialization.Serializable

@Serializable
data class BossResponse(
    val id: String,
    val name: String,
    val image: String? = null,
    val description: String? = null,
    val location: String? = null,
    val drops: List<String> = emptyList(),
    val healthPoints: String? = null
) {
    fun mapToBoss() = Boss(
        id = id,
        name = name,
        image = image,
        description = description,
        location = location,
        drops = drops,
        healthPoints = healthPoints
    )
}
