package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

import com.septianbeneran.urithiru.core.entity.eldenring.Npc
import com.septianbeneran.urithiru.core.util.extension.cleaned
import kotlinx.serialization.Serializable

@Serializable
data class NpcResponse(
    val id: String,
    val name: String,
    val image: String? = null,
    val quote: String? = null,
    val location: String? = null,
    val role: String? = null
) : CompendiumResponse {
    override fun mapToEntry() = Npc(
        id = id,
        name = name.trim(),
        image = image,
        quote = quote.cleaned(),
        location = location.cleaned(),
        role = role.cleaned()
    )
}
