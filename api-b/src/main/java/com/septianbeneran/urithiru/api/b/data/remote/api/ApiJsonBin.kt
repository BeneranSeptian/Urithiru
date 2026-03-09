package com.septianbeneran.urithiru.api.b.data.remote.api

import com.septianbeneran.urithiru.api.b.data.remote.dto.JsonBinDto
import com.septianbeneran.urithiru.api.b.data.remote.dto.OnBoardingPageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiJsonBin {
    @GET
    suspend fun getOnBoardingPageDataList(
        @Url url: String
    ): Response<JsonBinDto<List<OnBoardingPageResponse>>>
}