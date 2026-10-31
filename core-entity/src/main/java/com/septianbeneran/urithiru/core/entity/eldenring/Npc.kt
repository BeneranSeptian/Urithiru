package com.septianbeneran.urithiru.core.entity.eldenring

/** The API has no description for NPCs, so [description] is always null; [quote] is what they say, when known. */
data class Npc(
    override val id: String,
    override val name: String,
    override val image: String?,
    val quote: String?,
    val location: String?,
    val role: String?
) : CompendiumEntry {
    override val description: String?
        get() = null

    override val kind: CompendiumKind
        get() = CompendiumKind.Npcs
}
