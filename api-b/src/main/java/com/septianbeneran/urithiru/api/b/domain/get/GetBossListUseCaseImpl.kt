package com.septianbeneran.urithiru.api.b.domain.get

import com.septianbeneran.urithiru.api.b.repository.BossRepository
import com.septianbeneran.urithiru.core.entity.b.Boss
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBossListUseCaseImpl @Inject constructor(
    private val repository: BossRepository
) : GetBossListUseCase {
    override fun invoke(): Flow<ApiResult<List<Boss>>> = repository.getBossList()

}