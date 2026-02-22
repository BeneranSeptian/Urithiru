package com.example.api_b.data.api

import com.example.api_b.data.dto.BossResponse
import com.septianbeneran.template.core.remote.entity.ApiDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface BossApi {
    @GET
    suspend fun getBossList(
        @Url url: String,
        @Query("limit") limit: Int?,
        @Query("name") name: String?,
        @Query("page") page: Int?
    ): Response<ApiDto<List<BossResponse>>>
}