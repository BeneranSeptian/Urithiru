package com.example.api_b.data.service

import com.example.api_b.data.api.BossApi
import com.example.api_b.data.dto.BossResponse
import com.septianbeneran.template.core.base.BaseDataSource
import com.septianbeneran.template.core.remote.entity.ApiDto
import com.septianbeneran.template.core.remote.entity.ApiResult
import javax.inject.Inject

class ApiBRemoteDataSourceImpl @Inject constructor(
    private val api: BossApi
) : ApiBRemoteDataSource, BaseDataSource() {
    override suspend fun getBossList(
        limit: Int?,
        name: String?,
        page: Int?
    ): ApiResult<ApiDto<List<BossResponse>>> = getResult {
        api.getBossList("bosses", limit, name, page)
    }
}