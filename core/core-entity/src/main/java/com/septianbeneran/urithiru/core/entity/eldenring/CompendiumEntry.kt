package com.septianbeneran.urithiru.core.entity.eldenring

/** Anything the compendium lists. A `when` over the subtypes is exhaustive, so a new kind can't be forgotten. */
sealed interface CompendiumEntry {
    val id: String
    val name: String
    val image: String?
    val description: String?
    val kind: CompendiumKind
}
