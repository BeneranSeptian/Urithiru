package com.septianbeneran.urithiru.core.entity.eldenring

data class Location(
    override val id: String,
    override val name: String,
    override val image: String?,
    override val description: String?,
    val region: String?
) : CompendiumEntry {
    override val kind: CompendiumKind
        get() = CompendiumKind.Locations
}
