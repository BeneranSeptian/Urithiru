package com.septianbeneran.urithiru.api.b.repository

import com.septianbeneran.urithiru.api.b.data.service.ApiBRemoteDataSource
import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.base.BaseRepository
import com.septianbeneran.urithiru.core.entity.b.Boss
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.resultFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BossRepositoryImpl @Inject constructor(
    private val remote: ApiBRemoteDataSource,
    private val dispatcher: CoroutineDispatcherProvider,
    @ApplicationScope private val applicationScope: CoroutineScope
) : BossRepository, BaseRepository(applicationScope) {
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