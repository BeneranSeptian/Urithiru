package com.septianbeneran.urithiru.api.a.data.remote.dto.common

import com.septianbeneran.urithiru.core.entity.a.common.Scaling
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
