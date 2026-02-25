package com.example.api.b.domain.get

import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core.entity.b.Boss
import kotlinx.coroutines.flow.Flow

interface GetBossListUseCase {
    operator fun invoke(): Flow<ApiResult<List<Boss>>>
}