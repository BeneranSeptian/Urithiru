package com.septianbeneran.urithiru.api.b.data.local

import com.septianbeneran.urithiru.core.entity.b.OnBoardingPageData
import com.septianbeneran.urithiru.core.util.datastore.BaseDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JsonBinCacheImpl @Inject constructor(
    private val baseDataStore: BaseDataStore
) : JsonBinCache {
    override suspend fun saveOnBoardingPageDataList(onBoardingPageDataList: List<OnBoardingPageData>) {
        baseDataStore.saveObject("ON_BOARDING_PAGE_DATA_LIST", onBoardingPageDataList)
    }

    override fun loadOnBoardingPageDataList() = baseDataStore.readObject<List<OnBoardingPageData>>(
        "ON_BOARDING_PAGE_DATA_LIST"
    ).map { it ?: emptyList() }
}