package com.septianbeneran.urithiru.core.entity.eldenring

data class Item(
    override val id: String,
    override val name: String,
    override val image: String?,
    override val description: String?,
    val type: String?,
    val effect: String?
) : CompendiumEntry {
    override val kind: CompendiumKind
        get() = CompendiumKind.Items
}
