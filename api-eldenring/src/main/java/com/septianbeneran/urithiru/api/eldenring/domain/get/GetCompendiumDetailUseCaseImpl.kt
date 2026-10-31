package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.api.eldenring.repository.CompendiumRepository
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompendiumDetailUseCaseImpl @Inject constructor(
    private val repo: CompendiumRepository
) : GetCompendiumDetailUseCase {
    override fun invoke(
        kind: CompendiumKind,
        id: String
    ): Flow<ApiResult<CompendiumEntry>> = repo.getDetail(kind, id)
}
