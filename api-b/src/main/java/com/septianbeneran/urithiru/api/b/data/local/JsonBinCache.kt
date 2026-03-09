package com.septianbeneran.urithiru.api.b.data.local

import com.septianbeneran.urithiru.core.entity.b.OnBoardingPageData
import kotlinx.coroutines.flow.Flow

interface JsonBinCache {
    suspend fun saveOnBoardingPageDataList(onBoardingPageDataList: List<OnBoardingPageData>)
    fun loadOnBoardingPageDataList(): Flow<List<OnBoardingPageData>>
}