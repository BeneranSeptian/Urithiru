package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumSearchResult
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

/**
 * Searches every section in [kinds] by name at once and groups the matches by section, closest names first.
 * Succeeds when at least one section answers, and fails only when all of them do.
 */
interface SearchCompendiumUseCase {
    operator fun invoke(
        query: String,
        kinds: Set<CompendiumKind> = CompendiumKind.entries.toSet(),
        perKind: Int = DEFAULT_PER_KIND
    ): Flow<ApiResult<CompendiumSearchResult>>

    companion object {
        const val DEFAULT_PER_KIND = 8
    }
}
