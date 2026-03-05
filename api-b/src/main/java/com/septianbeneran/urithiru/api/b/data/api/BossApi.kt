package com.septianbeneran.urithiru.api.b.data.api

import com.septianbeneran.urithiru.api.b.data.dto.BossResponse
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
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