package com.septianbeneran.template.api.a.data.remote.service

import com.septianbeneran.template.api.a.BuildConfig.WEAPONS_V1
import com.septianbeneran.template.api.a.data.remote.api.WeaponApi
import com.septianbeneran.template.api.a.data.remote.dto.WeaponResponse
import com.septianbeneran.template.core.base.BaseDataSource
import com.septianbeneran.template.core.remote.entity.ApiDto
import com.septianbeneran.template.core.remote.entity.ApiResult
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