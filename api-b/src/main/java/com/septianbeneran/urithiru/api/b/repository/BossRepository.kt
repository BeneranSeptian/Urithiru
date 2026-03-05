package com.septianbeneran.urithiru.api.b.repository

import com.septianbeneran.urithiru.core.entity.b.Boss
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface BossRepository {

    fun getBossList(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): Flow<ApiResult<List<Boss>>>
}