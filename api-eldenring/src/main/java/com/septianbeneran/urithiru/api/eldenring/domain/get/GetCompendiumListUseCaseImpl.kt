package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.api.eldenring.repository.CompendiumRepository
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompendiumListUseCaseImpl @Inject constructor(
    private val repo: CompendiumRepository
) : GetCompendiumListUseCase {
    override fun invoke(
        kind: CompendiumKind,
        limit: Int?,
        name: String?,
        page: Int?
    ): Flow<ApiResult<CompendiumPage>> = repo.getList(kind, limit, name, page)
}
