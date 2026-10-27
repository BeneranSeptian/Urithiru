package com.septianbeneran.urithiru.core.entity.eldenring

import com.septianbeneran.urithiru.core.entity.eldenring.common.Attributes
import com.septianbeneran.urithiru.core.entity.eldenring.common.Scaling
import kotlinx.serialization.Serializable

@Serializable
data class Weapon(
    val id: String,
    val name: String,
    val description: String? = null,
    val image: String? = null,
    val attack: List<Attributes>,
    val defence: List<Attributes>,
    val scalesWith: List<Scaling>,
    val requiredAttributes: List<Attributes>,
    val category: String,
    val weight: Float
)
