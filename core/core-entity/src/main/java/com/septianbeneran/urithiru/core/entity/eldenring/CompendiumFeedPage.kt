package com.septianbeneran.urithiru.core.entity.eldenring

/**
 * One page of the mixed Explore feed. [endedKinds] have no more pages, so later pages should skip them.
 * [failedKinds] couldn't be loaded this time; the page still holds every section that did load.
 */
data class CompendiumFeedPage(
    val entries: List<CompendiumEntry>,
    val endedKinds: Set<CompendiumKind>,
    val failedKinds: Set<CompendiumKind>
)
