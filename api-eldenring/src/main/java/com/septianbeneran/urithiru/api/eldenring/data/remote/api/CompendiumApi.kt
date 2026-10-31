package com.septianbeneran.urithiru.api.eldenring.data.remote.api

import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.ArmorResponse
import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.BossResponse
import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.ItemResponse
import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.LocationResponse
import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.NpcResponse
import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.WeaponResponse
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface CompendiumApi {
    @GET
    suspend fun getBossList(
        @Url url: String,
        @Query("limit") limit: Int?,
        @Query("name") name: String?,
        @Query("page") page: Int?
    ): Response<ApiDto<List<BossResponse>>>

    @GET
    suspend fun getBossDetail(
        @Url url: String
    ): Response<ApiDto<BossResponse>>

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

    @GET
    suspend fun getArmorList(
        @Url url: String,
        @Query("limit") limit: Int?,
        @Query("name") name: String?,
        @Query("page") page: Int?
    ): Response<ApiDto<List<ArmorResponse>>>

    @GET
    suspend fun getArmorDetail(
        @Url url: String
    ): Response<ApiDto<ArmorResponse>>

    @GET
    suspend fun getItemList(
        @Url url: String,
        @Query("limit") limit: Int?,
        @Query("name") name: String?,
        @Query("page") page: Int?
    ): Response<ApiDto<List<ItemResponse>>>

    @GET
    suspend fun getItemDetail(
        @Url url: String
    ): Response<ApiDto<ItemResponse>>

    @GET
    suspend fun getLocationList(
        @Url url: String,
        @Query("limit") limit: Int?,
        @Query("name") name: String?,
        @Query("page") page: Int?
    ): Response<ApiDto<List<LocationResponse>>>

    @GET
    suspend fun getLocationDetail(
        @Url url: String
    ): Response<ApiDto<LocationResponse>>

    @GET
    suspend fun getNpcList(
        @Url url: String,
        @Query("limit") limit: Int?,
        @Query("name") name: String?,
        @Query("page") page: Int?
    ): Response<ApiDto<List<NpcResponse>>>

    @GET
    suspend fun getNpcDetail(
        @Url url: String
    ): Response<ApiDto<NpcResponse>>
}
