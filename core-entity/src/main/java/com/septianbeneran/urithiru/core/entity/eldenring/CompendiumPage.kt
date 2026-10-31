package com.septianbeneran.urithiru.core.entity.eldenring

/** One page of a compendium section. [total] is the size of the whole section, when the API reports it. */
data class CompendiumPage(
    val entries: List<CompendiumEntry>,
    val total: Int?
)
