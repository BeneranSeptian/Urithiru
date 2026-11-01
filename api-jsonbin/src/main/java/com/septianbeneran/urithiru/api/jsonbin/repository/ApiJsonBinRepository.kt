package com.septianbeneran.urithiru.api.jsonbin.repository

import com.septianbeneran.urithiru.api.jsonbin.data.local.JsonBinCache
import com.septianbeneran.urithiru.core.entity.onboarding.OnBoardingPageData
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface ApiJsonBinRepository {
    val cache: JsonBinCache
    fun getOnBoardingPageDataList(
        binId: String
    ): Flow<ApiResult<List<OnBoardingPageData>>>
}