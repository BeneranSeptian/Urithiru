package com.septianbeneran.urithiru.api.b.data.service

import com.septianbeneran.urithiru.api.b.BuildConfig.BOSSES_V1
import com.septianbeneran.urithiru.api.b.data.api.BossApi
import com.septianbeneran.urithiru.api.b.data.dto.BossResponse
import com.septianbeneran.urithiru.core.base.BaseDataSource
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import javax.inject.Inject

class ApiBRemoteDataSourceImpl @Inject constructor(
    private val api: BossApi
) : ApiBRemoteDataSource, BaseDataSource() {
    override suspend fun getBossList(
        limit: Int?,
        name: String?,
        page: Int?
    ): ApiResult<ApiDto<List<BossResponse>>> = getResult {
        api.getBossList(BOSSES_V1, limit, name, page)
    }
}