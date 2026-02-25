package com.example.api.b.repository

import com.example.api.b.data.service.ApiBRemoteDataSource
import com.septianbeneran.template.core.base.BaseRepository
import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core.util.CoroutineDispatcherProvider
import com.septianbeneran.template.core.util.resultFlow
import com.septianbeneran.template.core.entity.b.Boss
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BossRepositoryImpl @Inject constructor(
    private val remote: ApiBRemoteDataSource,
    private val dispatcher: CoroutineDispatcherProvider
) : BossRepository, BaseRepository() {
    override fun getBossList(
        limit: Int?,
        name: String?,
        page: Int?
    ): Flow<ApiResult<List<Boss>>> = resultFlow(
        networkCall = { remote.getBossList(limit, name, page) },
        dispatcher = dispatcher
    ).mapToEntity(
        transform = { it?.data?.map { bossResponse -> bossResponse.mapToBoss() } }
    )
}