package com.septianbeneran.urithiru.api.eldenring.data.remote.service

import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.CompendiumResponse
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import com.septianbeneran.urithiru.core.remote.entity.ApiResult

interface CompendiumRemoteDataSource {

    suspend fun getList(
        kind: CompendiumKind,
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): ApiResult<ApiDto<List<CompendiumResponse>>>

    suspend fun getDetail(
        kind: CompendiumKind,
        id: String
    ): ApiResult<ApiDto<CompendiumResponse>>
}
