package com.septianbeneran.urithiru.api.eldenring.repository

import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface CompendiumRepository {
    fun getList(
        kind: CompendiumKind,
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): Flow<ApiResult<CompendiumPage>>

    fun getDetail(
        kind: CompendiumKind,
        id: String
    ): Flow<ApiResult<CompendiumEntry>>
}
