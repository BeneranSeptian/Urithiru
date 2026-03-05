package com.septianbeneran.urithiru.api.a.data.remote.service

import com.septianbeneran.urithiru.api.a.BuildConfig.WEAPONS_V1
import com.septianbeneran.urithiru.api.a.data.remote.api.WeaponApi
import com.septianbeneran.urithiru.api.a.data.remote.dto.WeaponResponse
import com.septianbeneran.urithiru.core.base.BaseDataSource
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import javax.inject.Inject

class ApiARemoteDataSourceImpl @Inject constructor(
    private val api: WeaponApi
) : ApiARemoteDataSource, BaseDataSource() {
    override suspend fun getWeaponList(
        limit: Int?,
        name: String?,
        page: Int?
    ): ApiResult<ApiDto<List<WeaponResponse>>> =
        getResult { api.getWeaponList(WEAPONS_V1, limit, name, page) }

    override suspend fun getWeaponDetail(weaponId: String): ApiResult<ApiDto<WeaponResponse>> =
        getResult { api.getWeaponDetail("${WEAPONS_V1}$weaponId") }
}