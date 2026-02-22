package com.septianbeneran.template.api_a.data.remote.dto.common

import com.septianbeneran.template.core_entity.a.common.Attributes
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