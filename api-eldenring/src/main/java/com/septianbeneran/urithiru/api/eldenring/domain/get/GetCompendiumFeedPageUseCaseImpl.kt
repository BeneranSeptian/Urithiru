package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.api.eldenring.repository.CompendiumRepository
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumFeedPage
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class GetCompendiumFeedPageUseCaseImpl @Inject constructor(
    private val repo: CompendiumRepository
) : GetCompendiumFeedPageUseCase {

    override fun invoke(
        page: Int,
        kinds: Set<CompendiumKind>,
        seed: Long,
        name: String?,
        perKind: Int
    ): Flow<ApiResult<CompendiumFeedPage>> = flow {
        emit(ApiResult.Loading())

        val sections = repo.loadSections(kinds, limit = perKind, name = name, page = page)
        val firstError = sections.firstError
        if (sections.loaded.isEmpty() && firstError != null) {
            emit(ApiResult.Error(firstError))
            return@flow
        }
        val loaded = sections.loaded

        emit(
            ApiResult.Success(
                CompendiumFeedPage(
                    entries = mixFeed(
                        byKind = loaded.mapValues { it.value.entries },
                        random = Random(seed + page)
                    ),
                    endedKinds = loaded.filter { (_, result) -> result.isLastPage(page, perKind) }.keys,
                    failedKinds = sections.failedKinds
                )
            )
        )
    }

    private fun CompendiumPage.isLastPage(page: Int, perKind: Int): Boolean =
        entries.size < perKind || total?.let { (page + 1) * perKind >= it } == true
}

/**
 * Interleaves the sections one entry at a time, in a section order shuffled by [random] for every round,
 * so no section clumps together and the order is stable for the same [random] seed.
 */
internal fun mixFeed(
    byKind: Map<CompendiumKind, List<CompendiumEntry>>,
    random: Random
): List<CompendiumEntry> {
    val queues = byKind.filterValues { it.isNotEmpty() }.mapValues { ArrayDeque(it.value) }
    val mixed = mutableListOf<CompendiumEntry>()
    var lastKind: CompendiumKind? = null

    while (queues.values.any { it.isNotEmpty() }) {
        val round = queues.filterValues { it.isNotEmpty() }.keys.sortedBy { it.ordinal }.shuffled(random).toMutableList()
        if (round.size > 1 && round.first() == lastKind) {
            round.add(round.removeAt(0))
        }
        round.forEach { kind ->
            mixed += queues.getValue(kind).removeFirst()
            lastKind = kind
        }
    }
    return mixed
}
