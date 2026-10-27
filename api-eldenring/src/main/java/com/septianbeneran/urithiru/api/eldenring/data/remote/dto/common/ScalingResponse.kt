package com.septianbeneran.urithiru.api.eldenring.data.remote.dto.common

import com.septianbeneran.urithiru.core.entity.eldenring.common.Scaling
import kotlinx.serialization.Serializable

@Serializable
data class ScalingResponse (
    val name: String?,
    val scaling: String?
) {
    fun mapToScaling() = Scaling(
        name = name,
        scaling = scaling
    )
}
