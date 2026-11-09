package com.septianbeneran.urithiru.api.jsonbin.data.local

import com.septianbeneran.urithiru.core.entity.onboarding.OnBoardingPageData
import kotlinx.coroutines.flow.Flow

interface JsonBinCache {
    suspend fun saveOnBoardingPageDataList(onBoardingPageDataList: List<OnBoardingPageData>)
    fun loadOnBoardingPageDataList(): Flow<List<OnBoardingPageData>>
}