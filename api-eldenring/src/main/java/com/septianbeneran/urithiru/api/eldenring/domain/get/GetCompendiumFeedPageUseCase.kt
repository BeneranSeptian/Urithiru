package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumFeedPage
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

/**
 * Loads [page] of every section in [kinds] at once and mixes them into one feed page. The mix depends only
 * on [seed] and [page], so the same feed comes back while scrolling; pass a new seed to reshuffle.
 * Succeeds when at least one section loads, and fails only when all of them do. [name] filters every section by name.
 */
interface GetCompendiumFeedPageUseCase {
    operator fun invoke(
        page: Int,
        kinds: Set<CompendiumKind>,
        seed: Long,
        name: String? = null,
        perKind: Int = DEFAULT_PER_KIND
    ): Flow<ApiResult<CompendiumFeedPage>>

    companion object {
        const val DEFAULT_PER_KIND = 6
    }
}
