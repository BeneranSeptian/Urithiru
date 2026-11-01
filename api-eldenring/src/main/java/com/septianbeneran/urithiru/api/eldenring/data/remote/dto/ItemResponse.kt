package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

import com.septianbeneran.urithiru.core.entity.eldenring.Item
import com.septianbeneran.urithiru.core.util.extension.cleaned
import kotlinx.serialization.Serializable

@Serializable
data class ItemResponse(
    val id: String,
    val name: String,
    val image: String? = null,
    val description: String? = null,
    val type: String? = null,
    val effect: String? = null
) : CompendiumResponse {
    override fun mapToEntry() = Item(
        id = id,
        name = name.trim(),
        image = image,
        description = description.cleaned(),
        type = type.cleaned(),
        effect = effect.cleaned()
    )
}
