package com.septianbeneran.template.api.a.data.remote.service

import com.septianbeneran.template.api.a.data.remote.dto.WeaponResponse
import com.septianbeneran.template.core.remote.entity.ApiDto
import com.septianbeneran.template.core.remote.entity.ApiResult

interface ApiARemoteDataSource {

    suspend fun getWeaponList(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): ApiResult<ApiDto<List<WeaponResponse>>>

    suspend fun getWeaponDetail(
        weaponId: String
    ): ApiResult<ApiDto<WeaponResponse>>
}