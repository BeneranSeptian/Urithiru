package com.septianbeneran.template.core_entity.weapon

import com.septianbeneran.template.core_entity.weapon.common.Attributes
import com.septianbeneran.template.core_entity.weapon.common.Scaling

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
