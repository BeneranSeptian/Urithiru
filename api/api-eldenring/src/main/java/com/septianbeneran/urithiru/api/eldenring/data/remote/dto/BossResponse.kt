package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

import com.septianbeneran.urithiru.core.entity.eldenring.Boss
import com.septianbeneran.urithiru.core.util.extension.cleaned
import kotlinx.serialization.Serializable

@Serializable
data class BossResponse(
    val id: String,
    val name: String,
    val image: String? = null,
    val description: String? = null,
    val region: String? = null,
    val location: String? = null,
    val drops: List<String> = emptyList(),
    val healthPoints: String? = null
) : CompendiumResponse {
    override fun mapToEntry() = Boss(
        id = id,
        name = name.trim(),
        image = image,
        description = description.cleaned(),
        region = region.cleanedRegion(),
        location = location.cleaned(),
        drops = drops.mapNotNull { it.cleaned() },
        healthPoints = healthPoints.cleaned()
    )
}
