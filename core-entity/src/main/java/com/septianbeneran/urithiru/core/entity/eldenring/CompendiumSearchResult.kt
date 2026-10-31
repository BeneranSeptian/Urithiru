package com.septianbeneran.urithiru.core.entity.eldenring

/**
 * A name search across the compendium, one group per section that matched, best matches first.
 * [topResult] is set when an entry's name equals or starts with the query, and is left out of [groups].
 */
data class CompendiumSearchResult(
    val topResult: CompendiumEntry?,
    val groups: List<CompendiumSearchGroup>,
    val failedKinds: Set<CompendiumKind>
)

/** [hasMore] means the section may have more matches than [entries] shows. */
data class CompendiumSearchGroup(
    val kind: CompendiumKind,
    val entries: List<CompendiumEntry>,
    val hasMore: Boolean
)
