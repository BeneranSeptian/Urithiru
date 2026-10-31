package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

import com.septianbeneran.urithiru.core.entity.eldenring.Location
import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    val id: String,
    val name: String,
    val image: String? = null,
    val description: String? = null,
    val region: String? = null
) : CompendiumResponse {
    override fun mapToEntry() = Location(
        id = id,
        name = name.trim(),
        image = image,
        description = description.cleaned(),
        region = region.cleanedRegion()
    )
}
