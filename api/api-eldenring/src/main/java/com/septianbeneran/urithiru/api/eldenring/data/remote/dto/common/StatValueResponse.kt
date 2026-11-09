package com.septianbeneran.urithiru.api.eldenring.data.remote.dto.common

import com.septianbeneran.urithiru.core.entity.eldenring.common.StatValue
import kotlinx.serialization.Serializable

@Serializable
data class StatValueResponse(
    val name: String,
    val amount: Float
) {
    fun mapToStatValue() = StatValue(
        name = name.trim(),
        amount = amount
    )
}
