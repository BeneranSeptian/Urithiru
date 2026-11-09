package com.septianbeneran.urithiru.core.entity.eldenring

data class Boss(
    override val id: String,
    override val name: String,
    override val image: String?,
    override val description: String?,
    val region: String?,
    val location: String?,
    val drops: List<String>,
    val healthPoints: String?
) : CompendiumEntry {
    override val kind: CompendiumKind
        get() = CompendiumKind.Bosses
}
