package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.api.eldenring.repository.CompendiumRepository
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumSearchGroup
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumSearchResult
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchCompendiumUseCaseImpl @Inject constructor(
    private val repo: CompendiumRepository
) : SearchCompendiumUseCase {

    override fun invoke(
        query: String,
        kinds: Set<CompendiumKind>,
        perKind: Int
    ): Flow<ApiResult<CompendiumSearchResult>> = flow {
        emit(ApiResult.Loading())

        val trimmed = query.trim()
        val sections = repo.loadSections(kinds.associateWith { 0 }, limit = perKind, name = trimmed)
        val firstError = sections.firstError
        if (sections.loaded.isEmpty() && firstError != null) {
            emit(ApiResult.Error(firstError))
            return@flow
        }

        val groups = sections.loaded
            .filterValues { it.entries.isNotEmpty() }
            .map { (kind, page) ->
                CompendiumSearchGroup(
                    kind = kind,
                    entries = page.entries.sortedBy { matchScore(it.name, trimmed) },
                    hasMore = page.entries.size >= perKind
                )
            }
            .sortedWith(compareBy({ matchScore(it.entries.first().name, trimmed) }, { it.kind.ordinal }))
        val topResult = groups.firstOrNull()?.entries?.first()
            ?.takeIf { matchScore(it.name, trimmed) <= STARTS_WITH }

        emit(
            ApiResult.Success(
                CompendiumSearchResult(
                    topResult = topResult,
                    groups = groups
                        .map { group -> group.copy(entries = group.entries - setOfNotNull(topResult)) }
                        .filter { it.entries.isNotEmpty() },
                    failedKinds = sections.failedKinds
                )
            )
        )
    }
}

private const val EXACT = 0
private const val STARTS_WITH = 1
private const val WORD_STARTS_WITH = 2
private const val CONTAINS = 3
private const val FUZZY = 4

/** Lower is closer: exact, then prefix, then a word prefix, then anywhere in the name. Anything else the API returned ranks last. */
internal fun matchScore(name: String, query: String): Int {
    val candidate = name.lowercase()
    val wanted = query.lowercase()
    return when {
        wanted.isEmpty() -> FUZZY
        candidate == wanted -> EXACT
        candidate.startsWith(wanted) -> STARTS_WITH
        candidate.split(' ', '-', '\'').any { it.startsWith(wanted) } -> WORD_STARTS_WITH
        wanted in candidate -> CONTAINS
        else -> FUZZY
    }
}
