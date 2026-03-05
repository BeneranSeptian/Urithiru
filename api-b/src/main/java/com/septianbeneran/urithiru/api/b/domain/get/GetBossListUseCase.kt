package com.septianbeneran.urithiru.api.b.domain.get

import com.septianbeneran.urithiru.core.entity.b.Boss
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface GetBossListUseCase {
    operator fun invoke(): Flow<ApiResult<List<Boss>>>
}