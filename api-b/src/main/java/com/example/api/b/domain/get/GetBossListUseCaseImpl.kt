package com.example.api.b.domain.get

import com.example.api.b.repository.BossRepository
import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core.entity.b.Boss
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBossListUseCaseImpl @Inject constructor(
    private val repository: BossRepository
) : GetBossListUseCase {
    override fun invoke(): Flow<ApiResult<List<Boss>>> = repository.getBossList()

}