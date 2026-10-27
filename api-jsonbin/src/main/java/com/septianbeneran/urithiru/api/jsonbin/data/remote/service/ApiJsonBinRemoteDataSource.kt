package com.septianbeneran.urithiru.api.jsonbin.data.remote.service

import com.septianbeneran.urithiru.api.jsonbin.data.remote.dto.JsonBinDto
import com.septianbeneran.urithiru.api.jsonbin.data.remote.dto.OnBoardingPageResponse
import com.septianbeneran.urithiru.core.remote.entity.ApiResult

interface ApiJsonBinRemoteDataSource {
    suspend fun getOnBoardingPageDataList(
        bindId: String
    ): ApiResult<JsonBinDto<List<OnBoardingPageResponse>>>
}