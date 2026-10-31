package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface GetCompendiumListUseCase {
    operator fun invoke(
        kind: CompendiumKind,
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): Flow<ApiResult<CompendiumPage>>
}
