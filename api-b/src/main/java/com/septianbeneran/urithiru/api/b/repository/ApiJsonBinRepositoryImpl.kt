package com.septianbeneran.urithiru.api.b.repository

import com.septianbeneran.urithiru.api.b.data.local.JsonBinCache
import com.septianbeneran.urithiru.api.b.data.remote.service.ApiJsonBinRemoteDataSource
import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.base.BaseRepository
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.resultFlow
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class ApiJsonBinRepositoryImpl @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    override val cache: JsonBinCache,
    private val remote: ApiJsonBinRemoteDataSource,
    private val dispatcher: CoroutineDispatcherProvider
) : ApiJsonBinRepository, BaseRepository(applicationScope) {
    override fun getOnBoardingPageDataList(binId: String) = resultFlow(
        networkCall = { remote.getOnBoardingPageDataList(binId) },
        dispatcher = dispatcher
    ).mapToEntity(
        transform = { it?.record?.map { onBoardingPageResponse -> onBoardingPageResponse.mapToEntity() } },
        saveResult = { it?.let { cache.saveOnBoardingPageDataList(it) } },
    )
}