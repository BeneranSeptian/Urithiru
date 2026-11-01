package com.septianbeneran.urithiru.core.entity.eldenring

import com.septianbeneran.urithiru.core.entity.eldenring.common.Attributes
import com.septianbeneran.urithiru.core.entity.eldenring.common.Scaling
import kotlinx.serialization.Serializable

@Serializable
data class Weapon(
    override val id: String,
    override val name: String,
    override val description: String? = null,
    override val image: String? = null,
    val attack: List<Attributes>,
    val defence: List<Attributes>,
    val scalesWith: List<Scaling>,
    val requiredAttributes: List<Attributes>,
    val category: String,
    val weight: Float
) : CompendiumEntry {
    override val kind: CompendiumKind
        get() = CompendiumKind.Weapons
}
