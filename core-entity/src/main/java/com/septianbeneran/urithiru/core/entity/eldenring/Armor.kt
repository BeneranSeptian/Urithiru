package com.septianbeneran.urithiru.core.entity.eldenring

import com.septianbeneran.urithiru.core.entity.eldenring.common.StatValue

data class Armor(
    override val id: String,
    override val name: String,
    override val image: String?,
    override val description: String?,
    val category: String?,
    val dmgNegation: List<StatValue>,
    val resistance: List<StatValue>,
    val weight: Float?
) : CompendiumEntry {
    override val kind: CompendiumKind
        get() = CompendiumKind.Armors
}
