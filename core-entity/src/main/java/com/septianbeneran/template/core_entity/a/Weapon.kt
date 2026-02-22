package com.septianbeneran.template.core_entity.a

import com.septianbeneran.template.core_entity.a.common.Attributes
import com.septianbeneran.template.core_entity.a.common.Scaling

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
