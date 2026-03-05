package com.septianbeneran.urithiru.api.a.data.remote.api

import com.septianbeneran.urithiru.api.a.data.remote.dto.WeaponResponse
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeaponApi {

    @GET
    suspend fun getWeaponList(
        @Url url: String,
        @Query("limit") limit: Int?,
        @Query("name") name: String?,
        @Query("page") page: Int?
    ): Response<ApiDto<List<WeaponResponse>>>

    @GET
    suspend fun getWeaponDetail(
        @Url url: String
    ): Response<ApiDto<WeaponResponse>>
}