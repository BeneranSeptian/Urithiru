package com.example.api_b.repository

import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core_entity.b.Boss
import kotlinx.coroutines.flow.Flow

interface BossRepository {

    fun getBossList(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): Flow<ApiResult<List<Boss>>>
}