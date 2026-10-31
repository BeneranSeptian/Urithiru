package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface GetCompendiumDetailUseCase {
    operator fun invoke(
        kind: CompendiumKind,
        id: String
    ): Flow<ApiResult<CompendiumEntry>>
}
