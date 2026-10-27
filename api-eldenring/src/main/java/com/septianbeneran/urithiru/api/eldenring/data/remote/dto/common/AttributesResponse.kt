package com.septianbeneran.urithiru.api.eldenring.data.remote.dto.common

import com.septianbeneran.urithiru.core.entity.eldenring.common.Attributes
import kotlinx.serialization.Serializable

@Serializable
data class AttributesResponse(
    val name: String,
    val amount: Int
) {
    fun mapToAttributes() = Attributes(
        name = name,
        amount = amount
    )
}