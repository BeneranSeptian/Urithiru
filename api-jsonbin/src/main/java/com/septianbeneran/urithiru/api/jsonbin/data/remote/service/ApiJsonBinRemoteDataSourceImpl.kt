package com.septianbeneran.urithiru.api.jsonbin.data.remote.service

import com.septianbeneran.urithiru.api.jsonbin.data.remote.api.ApiJsonBin
import com.septianbeneran.urithiru.core.base.BaseDataSource
import javax.inject.Inject

class ApiJsonBinRemoteDataSourceImpl @Inject constructor(
    private val api: ApiJsonBin
) : ApiJsonBinRemoteDataSource, BaseDataSource() {
    override suspend fun getOnBoardingPageDataList(bindId: String) = getResult {
        api.getOnBoardingPageDataList("b/$bindId")
    }
}