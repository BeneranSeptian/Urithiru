package com.septianbeneran.urithiru.api.b.data.service

import com.septianbeneran.urithiru.api.b.data.dto.BossResponse
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import com.septianbeneran.urithiru.core.remote.entity.ApiResult

interface ApiBRemoteDataSource {
    suspend fun getBossList(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): ApiResult<ApiDto<List<BossResponse>>>
}