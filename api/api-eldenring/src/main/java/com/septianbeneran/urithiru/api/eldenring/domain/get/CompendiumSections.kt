package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.api.eldenring.repository.CompendiumRepository
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

/** The outcome of asking several sections for a page at the same time. */
internal class SectionResults(
    val loaded: Map<CompendiumKind, CompendiumPage>,
    val failedKinds: Set<CompendiumKind>,
    val firstError: AppError?
)

/** Requests, in parallel, the given page of every section in [pages] and waits for all of them to finish. */
internal suspend fun CompendiumRepository.loadSections(
    pages: Map<CompendiumKind, Int>,
    limit: Int,
    name: String?
): SectionResults {
    val results = coroutineScope {
        pages.map { (kind, page) ->
            async { kind to getList(kind, limit = limit, name = name, page = page).first { it !is ApiResult.Loading } }
        }.awaitAll()
    }

    val loaded = results.mapNotNull { (kind, result) ->
        (result as? ApiResult.Success)?.let { kind to (it.data ?: CompendiumPage(emptyList(), null)) }
    }.toMap()

    return SectionResults(
        loaded = loaded,
        failedKinds = pages.keys - loaded.keys,
        firstError = results.firstNotNullOfOrNull { (_, result) -> (result as? ApiResult.Error)?.error }
    )
}
