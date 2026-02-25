package com.example.api.b.data.service

import com.example.api.b.data.dto.BossResponse
import com.septianbeneran.template.core.remote.entity.ApiDto
import com.septianbeneran.template.core.remote.entity.ApiResult

interface ApiBRemoteDataSource {
    suspend fun getBossList(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): ApiResult<ApiDto<List<BossResponse>>>
}